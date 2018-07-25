package com.passport.peer;

import com.passport.msghandler.StrategyContext;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class ClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ChannelGroup getChannels() {
        return channels;
    }

    @Autowired
    private StrategyContext strategyContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("client read data客户端读到的数据是：{}", GsonUtils.toJson(message));
        strategyContext.handleRespMsgMain(ctx, message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client channel active客户端通道激活");

        //保存连接的channel
        channels.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

