package com.passport.peer;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理请求出去的channel或者连接进来的channel
 * @author: xujianfeng
 * @create: 2018-08-14 09:35
 **/
@Component
public class ChannelsManager {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsManager.class);

    public static ConcurrentHashMap<String,Integer> concurrentHashMap = new ConcurrentHashMap();

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ChannelGroup getChannels() {
        return channels;
    }

    public void addChannel(Channel channel){
        channels.add(channel);
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        concurrentHashMap.put(inetSocketAddress.getAddress().getHostAddress(),0);
        logger.info("增加channel实例后，实例数量："+channels.size());
    }

    public boolean remove(Channel channel){
        return channels.remove(channel);
    }
}
