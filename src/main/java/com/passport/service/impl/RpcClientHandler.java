package com.passport.service.impl;

import com.passport.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

/**
 * 处理RPC请求
 * @author: xujianfeng
 * @create: 2018-07-05 19:04
 **/
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

  private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

  /**
   * 存放 请求编号 与 响应对象 之间的映射关系
   */
  private ConcurrentMap<String, RpcResponse> responseMap;

  public RpcClientHandler(ConcurrentMap<String, RpcResponse> responseMap) {
    this.responseMap = responseMap;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
    // 建立 请求编号 与 响应对象 之间的映射关系
    responseMap.put(response.getRequestId(), response);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.error("client caught exception", cause);
    ctx.close();
  }
}
