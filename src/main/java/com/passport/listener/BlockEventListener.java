package com.passport.listener;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.peer.ClientHandler;
import com.passport.proto.*;
import com.passport.utils.CastUtils;
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

	/**
	 * 同步下一个区块
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
		blockBuilder.setBlockHeight(blockHeight+1);
		NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
		dataBuilder.setDataType(DataTypeEnum.DataType.BLOCK_SYNC);
		dataBuilder.setBlock(blockBuilder);

		NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
		builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
		builder.setData(dataBuilder);
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

		return block;
	}
}
