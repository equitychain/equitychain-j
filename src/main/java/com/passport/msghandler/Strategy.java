package com.passport.msghandler;

import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;

<<<<<<< HEAD
/**
 * 消息分类处理
 * @author: xujianfeng
 * @create: 2018-07-18 14:05
 **/
public abstract class Strategy {
    protected String channelType;
    /**
     * 处理请求消息
     * @param message
     */
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message, String channelType) throws Exception {
        this.channelType = channelType;
        handleMsg(ctx,message);
    }
    /**
     * 处理请求消息
     * @param message
     */
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {}
=======

public abstract class Strategy {

  void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
  }


  void handleRespMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
