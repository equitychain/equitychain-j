package com.passport.peer;

import com.google.gson.Gson;
import com.passport.proto.PersonModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<PersonModel.Person> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PersonModel.Person person) throws Exception {
        System.out.println("客户端收的的信息是: " + new Gson().toJson(person));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端通道激活");
        PersonModel.Person.Builder builder = PersonModel.Person.newBuilder();
        builder.setId(1);
        builder.setName("rose from client");
        builder.setEmail("rose@126.com");
        PersonModel.Person build = builder.build();
        ctx.channel().writeAndFlush(build);
        ctx.channel().writeAndFlush(build);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

