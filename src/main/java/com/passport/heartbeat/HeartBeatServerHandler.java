package com.passport.heartbeat;

import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatServerHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.debug("心跳服务端读到的数据是：{}", GsonUtils.toJson(message));
        //握手成功后发送定时心跳包
        if(message != null && message.getMessageType().equals(MessageTypeEnum.MessageType.HEARTBEAT_REQ)) {
            NettyMessage.Message heartBeat = buildHeatBeat();
            ctx.writeAndFlush(heartBeat);
        }else{
            ctx.fireChannelRead(message);
        }
    }

    private NettyMessage.Message buildHeatBeat() {
        //组装请求数据
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.HEART_BEAT);//心跳类型数据

        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setMessageType(MessageTypeEnum.MessageType.HEARTBEAT_RESP);//心跳响应消息
        builder.setData(dataBuilder);
        return builder.build();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("服务端通道激活");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
