package com.passport.peer;

import com.passport.annotations.RocksTransaction;
import com.passport.constant.NodeListConstant;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.GenerateBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import com.passport.utils.HttpUtils;
import com.passport.zookeeper.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: xujianfeng
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
    @Autowired
    private DBAccess dbAccess;

    @Override
    public void afterPropertiesSet() throws Exception {
        //启动服务并注册到discover节点
        asyncTask.startServer();

        TimeUnit.MILLISECONDS.sleep(3000);

        //连接discover节点
        Set<String> set = nodeListConstant.getAll();
        logger.info("注册后从discover节点取到的地址列表：{}", GsonUtils.toJson(set));
        String serviceAddress = HttpUtils.getLocalHostLANAddress().getHostAddress();
        for (String address : set) {
            if(!address.equals(serviceAddress)){
                asyncTask.startConnect(address);
            }
        }
    }

    //启动的时候自动开始区块同步
    @EventListener(ApplicationReadyEvent.class)
    public void syncNextBlock() {
        //请求最新区块
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNTLIST_SYNC);

        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
        builder.setData(dataBuilder);
        channelsManager.getChannels().writeAndFlush(builder.build());

        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        provider.publishEvent(new SyncNextBlockEvent(0L));

        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        provider.publishEvent(new GenerateBlockEvent(0L));

    }

    //生成下一个区块
    //@EventListener(ApplicationReadyEvent.class)
    public void generateNextBlock() {
        provider.publishEvent(new GenerateBlockEvent(0L));
    }

   // @EventListener(ApplicationReadyEvent.class)
    public void syncAccountList() {

    }
}
