package com.passport.msghandler;

import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class StrategyContext {

  private final Map<String, Strategy> strategyMap = new ConcurrentHashMap<>();

  @Autowired
  public StrategyContext(Map<String, Strategy> strategyMap) {
    this.strategyMap.clear();
    strategyMap.forEach((k, v) -> this.strategyMap.put(k, v));
  }

  public void handleReqMsgMain(ChannelHandlerContext ctx, NettyMessage.Message message) {
    System.out.println(message.getMessageType());
    System.out.println(message.getData().getDataType());
    Strategy strategy = strategyMap
        .get(message.getMessageType() + "_" + message.getData().getDataType());
    if (strategy != null) {
      strategy.handleReqMsg(ctx, message);
    } else {
      ctx.fireChannelRead(message);
    }
  }

  public void handleRespMsgMain(ChannelHandlerContext ctx, NettyMessage.Message message) {
    System.out.println(message.getMessageType());
    System.out.println(message.getData().getDataType());
    Strategy strategy = strategyMap
        .get(message.getMessageType() + "_" + message.getData().getDataType());
    if (strategy != null) {
      strategy.handleRespMsg(ctx, message);
    } else {
      ctx.fireChannelRead(message);
    }
  }
}
