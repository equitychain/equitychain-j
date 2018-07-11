package com.passport.peer;

import com.passport.proto.PersonModel;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends SimpleChannelInboundHandler<PersonModel.Person> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PersonModel.Person person) throws Exception {
        logger.debug("客户端读到的数据是：{}"+GsonUtils.toJson(person));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端通道激活");
        PersonModel.Person.Builder builder = PersonModel.Person.newBuilder();
        builder.setId(1);
        builder.setName("rose from client");
        builder.setEmail("rose@126.com");
        PersonModel.Person build = builder.build();
        ctx.channel().writeAndFlush(build);
        //ctx.channel().writeAndFlush(build);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

