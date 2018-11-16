package com.passport.peer;

import com.google.common.base.Optional;
import com.passport.constant.SyncFlag;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.event.GenerateBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.msghandler.StrategyContext;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.BlockUtils;
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

@ChannelHandler.Sharable
@Component
public class ClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final String channelType = "CLIENT_CHANNEL";
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
//                logger.info("服务端异常，已断开");
//                exceptionCaught(ctx,new Throwable());
                NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
                dataBuilder.setDataType(DataTypeEnum.DataType.HEART_BEAT);
                NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
                builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
                builder.setData(dataBuilder.build());
                ctx.writeAndFlush(builder.build());
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        int nextBlock = 0;
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        List<String> clientIpToAddress = dbAccess.seekByKey(clientIP);
        List<Trustee> trustees = dbAccess.listTrustees();
        Long lastBlockHeight = Long.valueOf(dbAccess.getLastBlockHeight().get().toString());
        int blockCycle = blockUtils.getBlockCycle(lastBlockHeight+1l);
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        List<Trustee> list = (List<Trustee>)objectOptional.get();
        for(String address:clientIpToAddress){
            dbAccess.rocksDB.delete((clientIP+"_"+address).getBytes());
            dbAccess.rocksDB.delete((address+"_"+clientIP).getBytes());
            for(Trustee trustee: trustees){
                //更新受托人列表
                if(trustee.getAddress().equals(address)){
                    trustee.setState(0);
                    SyncFlag.waitMiner.remove(address);
                    dbAccess.putTrustee(trustee);
                }
                //更新当前周期
                for(int i= 0;i<list.size();i++){
                    if(list.get(i).getAddress().equals(address)){
                        list.get(i).setStatus(0);
                        nextBlock = i+1;
                    }
                }
            }
        }
        Set<String> strings = storyFileUtil.getAddresses();
        if(list.size()<nextBlock){//最后一个的话则算出下个周期的用户看自己存不存在
            List<Trustee> newTrustees = trusteeHandler.findValidTrustees(blockCycle+1);
            if(newTrustees.size() == 0){
                newTrustees = trusteeHandler.getTrusteesBeforeTime(lastBlockHeight+1, blockCycle+1);
                for(String s: strings){
                    if(s.equals(newTrustees.get(0))){
                        provider.publishEvent(new GenerateBlockEvent(0L));
                    }
                }
            }
        }else{//节点异常算出下个出块用户是不是自己是则出块
            for(String s: strings){
                if(s.equals(list.get(nextBlock))){
                    provider.publishEvent(new GenerateBlockEvent(0L));
                }
            }
        }
        dbAccess.put(String.valueOf(blockCycle), list);
        logger.info(ctx.channel().remoteAddress().toString()+"服务端关闭");
        //重铸机制测试
        ctx.close();
    }
}

