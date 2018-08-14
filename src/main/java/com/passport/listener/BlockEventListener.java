package com.passport.listener;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.peer.ClientHandler;
import com.passport.proto.*;
import com.passport.utils.CastUtils;
import com.passport.webhandler.BlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 监听器处理区块事件
 */
@Component
public class BlockEventListener {
	private static Logger logger = LoggerFactory.getLogger(BlockEventListener.class);

	//TODO 后面使用算法生成前创建块的hashPrevBlock和hashMerkleRoot
	private final static String hashPrevBlock = "00000000000000000000000000000000000000000000000000000000000000";
	private final static String hashMerkleRoot = "1111111111111111111111111111111111111111111111111111111111111";

	@Autowired
	private DBAccess dbAccess;
	@Autowired
	private ClientHandler clientHandler;
	@Autowired
	private BlockHandler blockHandler;

	/**
	 * 同步下一个区块（被动获取）
	 * @param event
	 */
	@EventListener(SyncNextBlockEvent.class)
	public void syncNextBlock(SyncNextBlockEvent event) {
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
		//todo 同步多少个高度
		blockBuilder.setBlockSize(10l);
		NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
		dataBuilder.setDataType(DataTypeEnum.DataType.NEXT_BLOCK_SYNC);
		dataBuilder.setBlock(blockBuilder.build());

		NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
		builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
		builder.setData(dataBuilder.build());
		clientHandler.getChannels().writeAndFlush(builder.build());
	}

	//创建创世块
	private Block createGenesisBlock() {
		BlockHeader header = new BlockHeader();
		header.setHashPrevBlock(hashPrevBlock.getBytes());
		header.setHashMerkleRoot(hashMerkleRoot.getBytes());
		header.setTimeStamp(System.currentTimeMillis());

		Block block = new Block();
		block.setBlockHeader(header);
		block.setTransactionCount(0);
		block.setTransactions(new CopyOnWriteArrayList<Transaction>());
		block.setBlockHeight(1L);
		block.calculateFieldValueWithHash();

		return block;
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
		clientHandler.getChannels().writeAndFlush(builder.build());
	}
}
