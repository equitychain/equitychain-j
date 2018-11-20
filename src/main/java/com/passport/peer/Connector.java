package com.passport.peer;

import com.google.protobuf.ByteString;
import com.passport.constant.NodeListConstant;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import com.passport.utils.HttpUtils;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
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
    private BaseDBAccess dbAccess;
    @Autowired
    private StoryFileUtil storyFileUtil;

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
            if (!address.equals(serviceAddress)) {
                asyncTask.startConnect(address);
            }
        }
    }

    //启动的时候自动开始区块同步
    @EventListener(ApplicationReadyEvent.class)
    public void syncNextBlock() {
        Set<String> localAddress = storyFileUtil.getAddresses();
        for(String address:localAddress){
            try {
                String clientIP = HttpUtils.getLocalHostLANAddress().getHostAddress();
                dbAccess.rocksDB.put( (clientIP+"_"+address).getBytes(),SerializeUtils.serialize(address));
                dbAccess.rocksDB.put( (address+"_"+clientIP).getBytes(), SerializeUtils.serialize(address));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //发送同步账户请求
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNTLIST_SYNC);

        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
        builder.setData(dataBuilder);
        channelsManager.getChannels().writeAndFlush(builder.build());
        //发送本地账户ip绑定
        NettyData.Data.Builder dataBuilder1 = NettyData.Data.newBuilder();
        dataBuilder1.setDataType(DataTypeEnum.DataType.ACCOUNTIP_SYNC);
        for (String address : localAddress) {
            AccountMessage.Account.Builder builder1 = AccountMessage.Account.newBuilder();
            builder1.setAddress(ByteString.copyFrom(address.getBytes()));
            dataBuilder1.addAccounts(builder1.build());
        }
        NettyMessage.Message.Builder builder1 = NettyMessage.Message.newBuilder();
        builder1.setData(dataBuilder1.build());
        builder1.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
        channelsManager.getChannels().writeAndFlush(builder1.build());

        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //请求最新区块
        provider.publishEvent(new SyncNextBlockEvent(0L));
//        provider.publishEvent(new GenerateBlockEvent(0L));
    }
}
