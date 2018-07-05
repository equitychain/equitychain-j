package com.passport.peer;

import com.google.gson.Gson;
import com.passport.proto.PersonModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<PersonModel.Person> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PersonModel.Person person) throws Exception {
        System.out.println("服务端收到的信息: " + new Gson().toJson(person));

        //写给客户端
        PersonModel.Person.Builder builder = PersonModel.Person.newBuilder();
        builder.setId(1);
        builder.setName("jack from server");
        builder.setEmail("jack@126.com");
        PersonModel.Person build = builder.build();
        ctx.channel().writeAndFlush(build);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端通道激活");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
