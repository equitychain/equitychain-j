package com.passport.peer;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.listener.ChannelListener;
import com.passport.msghandler.StrategyContext;
import com.passport.proto.NettyMessage;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.TrusteeHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
@Component
public class ServerHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final String channelType = "SERVER_CHANNEL";
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Autowired
    ChannelListener listener;
    @Autowired
    private ChannelsManager channelsManager;
    @Autowired
    private StrategyContext strategyContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("server read data服务端读到的数据是：{}", GsonUtils.toJson(message));
        strategyContext.handleMsgMain(ctx, message,channelType);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channel active服务端通道激活");

        logger.info("server channel id:"+ctx.channel().id().asLongText());
        listener.channelActive(ctx);
        //保存连接的channel
        channelsManager.addChannel(ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        listener.channelClose(ctx);
        ctx.close();
    }
}
