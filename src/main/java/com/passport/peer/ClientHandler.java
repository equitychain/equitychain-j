package com.passport.peer;

import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.debug("客户端读到的数据是：{}"+GsonUtils.toJson(message));
        ctx.fireChannelRead(message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端通道激活");
        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();

        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
        blockBuilder.setBlockHeight(1000L);
        blockBuilder.setBlockSize(1000);
        blockBuilder.setTotalAmount(100L);
        blockBuilder.setTotalFee(100);
        dataBuilder.setBlock(blockBuilder);
        dataBuilder.setDataType(DataTypeEnum.DataType.BLOCK_SYNC);

        builder.setData(dataBuilder);
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
        ctx.channel().writeAndFlush(builder.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

