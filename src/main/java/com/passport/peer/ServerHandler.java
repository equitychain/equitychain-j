package com.passport.peer;

import com.google.common.base.Optional;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
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
        logger.info(channelsManager.concurrentHashMap.get(ctx.channel().id().toString())+"已经30秒未收到客户端的消息了！"+channelsManager.getChannels().size());
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                channelsManager.concurrentHashMap.put(ctx.channel().id().toString(),channelsManager.concurrentHashMap.get(ctx.channel().id().toString())+1);
                if(channelsManager.concurrentHashMap.get(ctx.channel().id().toString())>=3){
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
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        List<String> ipAddress = dbAccess.seekByKey(clientIP);
        List<Trustee> trustees = dbAccess.listTrustees();
        int blockCycle = blockUtils.getBlockCycle(Long.valueOf(dbAccess.getLastBlockHeight().get().toString())+1l);
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        List<Trustee> list = (List<Trustee>)objectOptional.get();
//        List<Trustee> list = (List<Trustee>)dbAccess.get(String.valueOf(blockCycle));
        for(String address:ipAddress){
            dbAccess.rocksDB.delete((clientIP+"_"+address).getBytes());
            dbAccess.rocksDB.delete((address+"_"+clientIP).getBytes());
            //更新受托人列表
            for(Trustee trustee: trustees){
                if(trustee.getAddress().equals(address)){
                    trustee.setState(0);
                    SyncFlag.waitMiner.remove(address);
                    dbAccess.putTrustee(trustee);
                }
            }
            //更新当前周期
            for(Trustee tee : list){
                if(tee.getAddress().equals(address)){
                    tee.setStatus(0);
                }
            }
        }
        dbAccess.put(String.valueOf(blockCycle), list);
        logger.info(ctx.channel().remoteAddress().toString()+"客户端关闭");
        ctx.close();
    }
}
