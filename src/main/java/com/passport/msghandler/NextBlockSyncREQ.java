package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.BlockMessage;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.webhandler.BlockHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("DATA_REQ_NEXT_BLOCK_SYNC")
public class NextBlockSyncREQ extends Strategy {

  private static final Logger logger = LoggerFactory.getLogger(NextBlockSyncREQ.class);

  @Autowired
  private DBAccess dbAccess;
  @Autowired
  private BlockHandler blockHandler;

  public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {

    long blockHeight = message.getData().getBlock().getBlockHeight();
    Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
    if (blockOptional.isPresent()) {
      Block block = blockOptional.get();

      BlockMessage.Block.Builder blockBuilder = blockHandler.convertBlock2BlockMessage(block);

      NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
      dataBuilder.setDataType(DataTypeEnum.DataType.NEXT_BLOCK_SYNC);
      dataBuilder.setBlock(blockBuilder.build());

      NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
      builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
      builder.setData(dataBuilder.build());
      ctx.writeAndFlush(builder.build());
    }
  }
}
