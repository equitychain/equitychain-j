package com.passport.heartbeat;

import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class HeartBeatClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {

  private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);

  private volatile ScheduledFuture<?> heartBeat;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message)
      throws Exception {
    if (message != null && message.getMessageType().equals(MessageTypeEnum.MessageType.DATA_RESP)) {
      heartBeat = ctx.executor().scheduleAtFixedRate(
          new HeartBeatClientHandler.HeartBeatTask(ctx),
          0,
          10000,
          TimeUnit.MILLISECONDS);
    } else if (message != null && message.getMessageType()
        .equals(MessageTypeEnum.MessageType.HEARTBEAT_RESP)) {
      logger.info("heartbeat message：{}", GsonUtils.toJson(message));
    } else {
      ctx.fireChannelRead(message);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    if (heartBeat != null) {
      heartBeat.cancel(true);
      heartBeat = null;
    }
    ctx.fireExceptionCaught(cause);
  }

  private class HeartBeatTask implements Runnable {

    private final ChannelHandlerContext ctx;

    public HeartBeatTask(final ChannelHandlerContext ctx) {
      this.ctx = ctx;
    }

    @Override
    public void run() {
      NettyMessage.Message message = buildHeatBeat();
      ctx.writeAndFlush(message);
    }

    private NettyMessage.Message buildHeatBeat() {
      NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
      dataBuilder.setDataType(DataTypeEnum.DataType.HEART_BEAT);

      NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
      builder.setMessageType(MessageTypeEnum.MessageType.HEARTBEAT_REQ);
      builder.setData(dataBuilder);
      return builder.build();
    }
  }
}

