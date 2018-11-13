package com.passport.peer;

import com.passport.msghandler.StrategyContext;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class ClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final String channelType = "CLIENT_CHANNEL";
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    private ChannelsManager channelsManager;
    @Autowired
    private StrategyContext strategyContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("client read data客户端读到的数据是：{}", GsonUtils.toJson(message));
        strategyContext.handleMsgMain(ctx, message,channelType);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client channel active客户端通道激活");

        logger.info("client channel id:"+ctx.channel().id().asLongText());
        //保存连接的channel
        channelsManager.addChannel(ctx.channel());
    }
    //管道中上一个Handler触发的事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("客户端心跳监测: "+channelsManager.getChannels().size());
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                logger.info("服务端异常，已断开");
                exceptionCaught(ctx,new Throwable());
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //重铸机制测试
        ctx.close();
    }
}

