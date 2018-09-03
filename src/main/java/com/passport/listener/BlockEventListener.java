package com.passport.listener;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.BlockMessage;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.CastUtils;
import com.passport.webhandler.BlockHandler;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class BlockEventListener {

  private final static String hashPrevBlock = "00000000000000000000000000000000000000000000000000000000000000";
  private final static String hashMerkleRoot = "1111111111111111111111111111111111111111111111111111111111111";
  private static Logger logger = LoggerFactory.getLogger(BlockEventListener.class);
  @Autowired
  private DBAccess dbAccess;
  @Autowired
  private ChannelsManager channelsManager;
  @Autowired
  private BlockHandler blockHandler;


  @EventListener(SyncNextBlockEvent.class)
  public void syncNextBlock(SyncNextBlockEvent event) {
    Long blockHeight = CastUtils.castLong(event.getSource());
    if (blockHeight == 0) {
      Optional<Object> lastBlockHeight = dbAccess.getLastBlockHeight();
      if (lastBlockHeight.isPresent()) {
        blockHeight = CastUtils.castLong(lastBlockHeight.get());
      } else {
        Block block = createGenesisBlock();
        blockHeight = block.getBlockHeight();

        dbAccess.putBlock(block);
        dbAccess.putLastBlockHeight(blockHeight);
      }
    }

    BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
    blockBuilder.setBlockHeight(blockHeight + 1);
    NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
    dataBuilder.setDataType(DataTypeEnum.DataType.NEXT_BLOCK_SYNC);
    dataBuilder.setBlock(blockBuilder.build());

    NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
    builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
    builder.setData(dataBuilder.build());
    channelsManager.getChannels().writeAndFlush(builder.build());
  }

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

  @EventListener(SyncBlockEvent.class)
  public void syncBlock(SyncBlockEvent event) {
    Block block = (Block) event.getSource();

    BlockMessage.Block.Builder blockBuilder = blockHandler.convertBlock2BlockMessage(block);

    NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
    dataBuilder.setDataType(DataTypeEnum.DataType.BLOCK_SYNC);
    dataBuilder.setBlock(blockBuilder.build());

    NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
    builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
    builder.setData(dataBuilder.build());
    channelsManager.getChannels().writeAndFlush(builder.build());
  }
}
