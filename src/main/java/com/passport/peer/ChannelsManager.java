package com.passport.peer;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ChannelsManager {

  private static final Logger logger = LoggerFactory.getLogger(ChannelsManager.class);
  private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  public ChannelGroup getChannels() {
    return channels;
  }

  public void addChannel(Channel channel) {
    channels.add(channel);
  }

  public boolean remove(Channel channel) {
    return channels.remove(channel);
  }
}
