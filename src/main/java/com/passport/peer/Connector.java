package com.passport.peer;

import com.passport.constant.NodeListConstant;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.HttpUtils;
import com.passport.zookeeper.ServiceRegistry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author: Bee xu
 * @create: 2018-07-05 17:21
 **/
@Component
public class Connector implements InitializingBean {

  private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

  @Autowired
  private ConnectAsync asyncTask;
  @Autowired
  private NodeListConstant nodeListConstant;
  @Autowired
  private ApplicationContextProvider provider;
  @Autowired
  private ChannelsManager channelsManager;

  @Override
  public void afterPropertiesSet() throws Exception {

    asyncTask.startServer();

    TimeUnit.SECONDS.sleep(3);

    Set<String> set = nodeListConstant.getAll();
    String serviceAddress = HttpUtils.getLocalHostLANAddress().getHostAddress();
    for (String address : set) {
      if (!address.equals(serviceAddress)) {
        asyncTask.startConnect(address);
      }
    }
  }


  @EventListener(ApplicationReadyEvent.class)
  public void syncNextBlock() {
    provider.publishEvent(new SyncNextBlockEvent(0L));
  }

  @EventListener(ApplicationReadyEvent.class)
  public void syncAccountList() {

    NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
    dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNTLIST_SYNC);

    NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
    builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
    builder.setData(dataBuilder);
    channelsManager.getChannels().writeAndFlush(builder.build());
  }
}
