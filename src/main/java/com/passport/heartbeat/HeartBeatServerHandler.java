package com.passport.heartbeat;

import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {

  private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message)
      throws Exception {
    if (message != null && message.getMessageType()
        .equals(MessageTypeEnum.MessageType.HEARTBEAT_REQ)) {
      NettyMessage.Message heartBeat = buildHeatBeat();
      ctx.writeAndFlush(heartBeat);
    } else {
      ctx.fireChannelRead(message);
    }
  }

  private NettyMessage.Message buildHeatBeat() {
    NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
    dataBuilder.setDataType(DataTypeEnum.DataType.HEART_BEAT);

    NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
    builder.setMessageType(MessageTypeEnum.MessageType.HEARTBEAT_RESP);
    builder.setData(dataBuilder);
    return builder.build();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.info("server channel inbound");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
