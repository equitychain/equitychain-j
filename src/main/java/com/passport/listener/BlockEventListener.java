package com.passport.listener;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.GenerateBlockEvent;
import com.passport.event.SyncBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.*;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.BlockHandler;
import com.passport.webhandler.MinerHandler;
import com.passport.webhandler.TrusteeHandler;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
	@Autowired
	private BlockUtils blockUtils;
	@Autowired
	private MinerHandler minerHandler;
	@Autowired
	private TrusteeHandler trusteeHandler;


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

	/**
	 * 由第一个节点发起出块
	 * @param event
	 */
	@EventListener(GenerateBlockEvent.class)
	public void generateBlock(GenerateBlockEvent event) {
		ChannelGroup channels = channelsManager.getChannels();
		//第一个启动的节点，负责生成区块
		if(channels.size() == 0){
			//当前区块周期
			Optional<Object> lastBlockHeightOptional = dbAccess.getLastBlockHeight();
			if(!lastBlockHeightOptional.isPresent()){
				return;
			}

			long blockHeight = CastUtils.castLong(lastBlockHeightOptional.get());
			if(blockHeight > 1){
				return;
			}

			Long timestamp = blockUtils.getTimestamp4BlockCycle(blockHeight + 1);
			//查询投票记录（status==1）,时间小于等于timestamp，按投票票数从高到低排列的101个受托人，放到101个受托人列表中
			List<Trustee> list = new ArrayList<>();//TODO

			//101个受托人放到本地存放，key为出块周期，下一个出块周期到来时，需要删除上一个出块周期的数据
			int blockCycle = blockUtils.getBlockCycle(blockHeight + 1);
			dbAccess.put(String.valueOf(blockCycle), list);//TODO

			Trustee trustee = blockUtils.randomPickBlockPruducer(list, blockHeight + 1);
			String address = trustee.getAddress();
			Optional<Account> accountOptional = dbAccess.getAccount(address);
			if(accountOptional.isPresent() && accountOptional.get().getPrivateKey() != null){//出块人属于本节点
				Account account = accountOptional.get();
				if(account.getPrivateKey() != null){
					//打包区块
					minerHandler.packagingBlock(account);

					//更新101个受托人，已经出块人的状态
					trusteeHandler.changeStatus(trustee, blockCycle);

					//出块完成后，计算出下一个出块人是不是自己，如果是则发布出块事件
					//查询出块周期内剩余出块者
					List<Trustee> trustees = trusteeHandler.findValidTrustees(blockCycle);
					Trustee tee = blockUtils.randomPickBlockPruducer(trustees, blockHeight + 2);
					Optional<Account> accOptional = dbAccess.getAccount(tee.getAddress());
					if(accOptional.isPresent() && accOptional.get().getPrivateKey() != null) {//出块人属于本节点
						//出块逻辑
					}
				}
			}
		}
	}
}
