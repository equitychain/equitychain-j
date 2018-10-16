package com.passport.msghandler;

import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息分类处理
 * @author: xujianfeng
 * @create: 2018-07-18 14:05
 **/
public abstract class Strategy {
    /**
     * 处理请求消息
     * @param message
     */
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {}
}
