package com.passport.peer;

import com.google.common.base.Optional;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.event.GenerateBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.msghandler.StrategyContext;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.utils.StoryFileUtil;
import com.passport.webhandler.TrusteeHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
@Component
public class ServerHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final String channelType = "SERVER_CHANNEL";
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Autowired
    private ChannelsManager channelsManager;
    @Autowired
    private StrategyContext strategyContext;
    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private StoryFileUtil storyFileUtil;
    @Autowired
    private ApplicationContextProvider provider;
    @Autowired
    private TrusteeHandler trusteeHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("server read data服务端读到的数据是：{}", GsonUtils.toJson(message));
        strategyContext.handleMsgMain(ctx, message,channelType);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channel active服务端通道激活");
        logger.info("server channel id:"+ctx.channel().id().asLongText());
        //保存连接的channel
        channelsManager.addChannel(ctx.channel());
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        logger.info(channelsManager.concurrentHashMap.get(ctx.channel().id().asShortText())+"已经30秒未收到客户端的消息了！剩余channel数"+channelsManager.getChannels().size());
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.ALL_IDLE){
                channelsManager.concurrentHashMap.put(ctx.channel().id().asShortText(),channelsManager.concurrentHashMap.get(ctx.channel().id().asShortText())+1);
                logger.info(ctx+"通道心跳数"+channelsManager.concurrentHashMap.get(ctx.channel().id().asShortText()));
                if(channelsManager.concurrentHashMap.get(ctx.channel().id().asShortText())>=3){
                    logger.info("关闭这个不活跃通道！");
                    exceptionCaught(ctx,new Throwable());
                }
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(ctx.channel().remoteAddress().toString()+"客户端关闭");
        ctx.close();
    }
}
