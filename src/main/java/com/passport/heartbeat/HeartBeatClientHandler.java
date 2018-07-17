package com.passport.heartbeat;

import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartBeatClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.debug("心跳客户端读到的数据是：{}"+GsonUtils.toJson(message));
        //握手成功后发送定时心跳包
        if(message != null && message.getMessageType().equals(MessageTypeEnum.MessageType.DATA_RESP)){
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatClientHandler.HeartBeatTask(ctx),
                    0,
                    5000,
                    TimeUnit.MILLISECONDS);
        }else if(message != null && message.getMessageType().equals(MessageTypeEnum.MessageType.HEARTBEAT_RESP)){
            logger.info("收到心跳回复消息：{}" + GsonUtils.toJson(message));
        }else{
            ctx.fireChannelRead(message);
        }
    }

    //构造异步心跳包
    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage.Message message = buildHeatBeat();
            ctx.writeAndFlush(message);
        }

        private NettyMessage.Message buildHeatBeat() {
            //组装请求数据
            NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
            dataBuilder.setDataType(DataTypeEnum.DataType.HEART_BEAT);//心跳类型数据

            NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
            builder.setMessageType(MessageTypeEnum.MessageType.HEARTBEAT_REQ);//心跳请求消息
            builder.setData(dataBuilder);
            return builder.build();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}

