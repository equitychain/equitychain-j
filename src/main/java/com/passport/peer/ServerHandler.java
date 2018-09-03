package com.passport.peer;

import com.passport.msghandler.StrategyContext;
import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class ServerHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {

  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

  @Autowired
  private ChannelsManager channelsManager;
  @Autowired
  private StrategyContext strategyContext;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message)
      throws Exception {
    strategyContext.handleReqMsgMain(ctx, message);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.info("server channel active");

    logger.info("server channel id:" + ctx.channel().id().asLongText());
    channelsManager.addChannel(ctx.channel());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
