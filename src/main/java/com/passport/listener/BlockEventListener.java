package com.passport.listener;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.GenesisBlockInfo;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.*;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.BlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * 监听器处理区块事件
 */
@Component
public class BlockEventListener {
	private static Logger logger = LoggerFactory.getLogger(BlockEventListener.class);

	//TODO 后面使用算法生成前创建块的hashPrevBlock和hashMerkleRoot
	private final static String hashPrevBlock = "bx0000000000000000000000000000000000000000000000000000000000000000";
	private final static String hashMerkleRoot = "bx0000000000000000000000000000000000000000000000000000000000000000";

	@Autowired
	private DBAccess dbAccess;
	@Autowired
	private ChannelsManager channelsManager;
	@Autowired
	private BlockHandler blockHandler;

	/**
	 * 同步下一个区块（被动获取）
	 * @param event
	 */
	@EventListener(SyncNextBlockEvent.class)
	public void syncNextBlock(SyncNextBlockEvent event) throws ParseException {
		Long blockHeight = CastUtils.castLong(event.getSource());
		if (blockHeight == 0) {
			//取出本地存储的最新区块高度
			Optional<Object> lastBlockHeight = dbAccess.getLastBlockHeight();
			if (lastBlockHeight.isPresent()) {
				blockHeight = CastUtils.castLong(lastBlockHeight.get());
			}else{
				//创建创世块
				Block block = createGenesisBlock();
				blockHeight = block.getBlockHeight();

				//保存区块到本地
				dbAccess.putBlock(block);
				//保存区块高度到本地
				dbAccess.putLastBlockHeight(blockHeight);
			}
		}
		//请求最新区块
		BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
		//那个区块开始
		blockBuilder.setBlockHeight(blockHeight+1);
		blockBuilder.setBlockNumber(Constant.BLOCK_SYNC_PERTIME);
		NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
		dataBuilder.setDataType(DataTypeEnum.DataType.NEXT_BLOCK_SYNC);
		dataBuilder.setBlock(blockBuilder.build());

		NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
		builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
		builder.setData(dataBuilder.build());
		channelsManager.getChannels().writeAndFlush(builder.build());
	}

	//创建创世块
	private Block createGenesisBlock() {
		BlockHeader header = new BlockHeader();
		header.setHashPrevBlock(hashPrevBlock.getBytes());
		header.setTimeStamp(Constant.GENESIS_BLOCK_TIMESTAMP);

		//从创世json文件中加载创世信息
		GenesisBlockInfo genesisBlockInfo = null;
		try {
			genesisBlockInfo = loadGenesisFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Block block = new Block();
		block.setBlockHeader(header);
		block.setTransactionCount(genesisBlockInfo.getTransactions().size());
		block.setTransactions(genesisBlockInfo.getTransactions());
		block.setBlockHeight(1L);
		block.calculateFieldValueWithHash();

		return block;
	}

	//从创世json文件中加载创世信息并存放到rocksdb中
	private GenesisBlockInfo loadGenesisFile() throws IOException {
		//读取项目下json文件，初始化
		BufferedReader reader = new BufferedReader(new FileReader(Constant.GENESIS_PATH));
		StringBuffer buffer = new StringBuffer();
		String tmp;
		while ((tmp = reader.readLine()) != null){
			buffer.append(tmp);
		}
		GenesisBlockInfo genesisBlockInfo = GsonUtils.fromJson(GenesisBlockInfo.class, buffer.toString());

		List<Account> accounts = genesisBlockInfo.getAccounts();
		accounts.forEach(account -> {
			if(!dbAccess.getAccount(account.getAddress()).isPresent()){
				dbAccess.putAccount(account);
			}
		});

		genesisBlockInfo.getTransactions().forEach(transaction -> {
			dbAccess.putConfirmTransaction(transaction);
		});

		genesisBlockInfo.getTrustees().forEach(trustee -> {
			dbAccess.putTrustee(trustee);
		});

		return genesisBlockInfo;
	}

	/**
	 * 同步区块到其它节点（主动广播）
	 * @param event
	 */
	@EventListener(SyncBlockEvent.class)
	public void syncBlock(SyncBlockEvent event) {
		//广播区块数据到其它节点
		Block block = (Block)event.getSource();

		BlockMessage.Block.Builder blockBuilder = blockHandler.convertBlock2BlockMessage(block);

		//构造区块同步响应消息
		NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
		dataBuilder.setDataType(DataTypeEnum.DataType.BLOCK_SYNC);
		dataBuilder.setBlock(blockBuilder.build());

		NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
		builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
		builder.setData(dataBuilder.build());
		channelsManager.getChannels().writeAndFlush(builder.build());
	}
}
