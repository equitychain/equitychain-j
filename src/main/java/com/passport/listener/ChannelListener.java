package com.passport.listener;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelListener {
    void channelActive(ChannelHandlerContext ctx);
    void channelClose(ChannelHandlerContext ctx) throws Exception;
}
