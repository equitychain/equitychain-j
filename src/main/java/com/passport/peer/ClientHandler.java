package com.passport.peer;

import com.passport.dto.RpcResponse;
import com.passport.proto.PersonModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentMap;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    /**
     * 存放 请求编号 与 响应对象 之间的映射关系
     */
    private ConcurrentMap<String, RpcResponse> responseMap;

    public ClientHandler(ConcurrentMap<String, RpcResponse> responseMap) {
        this.responseMap = responseMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        // 建立 请求编号 与 响应对象 之间的映射关系
        responseMap.put(response.getRequestId(), response);
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

