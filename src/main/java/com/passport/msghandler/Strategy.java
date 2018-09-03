package com.passport.msghandler;

import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;


public abstract class Strategy {

  void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
  }


  void handleRespMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
  }
}
