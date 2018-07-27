package com.passport.msghandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息分类处理
 * @author: xujianfeng
 * @create: 2018-07-18 14:05
 **/
public abstract class Strategy {
    /**
     * 服务端处理请求消息
     * @param message
     */
    void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message){}

    /**
     * 客户端处理响应消息
     * @param message
     */
    void handleRespMsg(ChannelHandlerContext ctx, NettyMessage.Message message){}
}
