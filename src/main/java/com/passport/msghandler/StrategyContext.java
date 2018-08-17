package com.passport.msghandler;

import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息路由入口
 * @author: xujianfeng
 * @create: 2018-07-18 14:06
 **/
@Component
public class StrategyContext {
    private final Map<String, Strategy> strategyMap = new ConcurrentHashMap<>();

    /**
     * 注入所有实现了Strategy接口的Bean
     * @param strategyMap
     */
    @Autowired
    public StrategyContext(Map<String, Strategy> strategyMap) {
        this.strategyMap.clear();
        strategyMap.forEach((k, v)-> this.strategyMap.put(k, v));
    }

    /**
     * 请求消息入口
     * @param ctx
     * @param message
     */
    public void handleMsgMain(ChannelHandlerContext ctx, NettyMessage.Message message) {
        System.out.println(message.getMessageType());
        System.out.println(message.getData().getDataType());
        Strategy strategy = strategyMap.get(message.getMessageType()+"_"+message.getData().getDataType());
        if(strategy != null){
            strategy.handleMsg(ctx, message);
        }else{
            ctx.fireChannelRead(message);
        }
    }

    /**
     * 应用请求消息路由到handleReqMsg
     * @param message
     */
    public void handleReqMsgMain(ChannelHandlerContext ctx, NettyMessage.Message message) {
        System.out.println(message.getMessageType());
        System.out.println(message.getData().getDataType());
        Strategy strategy = strategyMap.get(message.getMessageType()+"_"+message.getData().getDataType());
        if(strategy != null){
            strategy.handleReqMsg(ctx, message);
        }else{
            ctx.fireChannelRead(message);
        }
    }

    /**
     * 应用响应消息路由到handleRespMsg
     * @param message
     */
    public void handleRespMsgMain(ChannelHandlerContext ctx, NettyMessage.Message message) {
        System.out.println(message.getMessageType());
        System.out.println(message.getData().getDataType());
        Strategy strategy = strategyMap.get(message.getMessageType()+"_"+message.getData().getDataType());
        if(strategy != null){
            strategy.handleRespMsg(ctx, message);
        }else{
            ctx.fireChannelRead(message);
        }
    }
}
