package com.passport.peer;

<<<<<<< HEAD
import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.constant.NodeListConstant;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import com.passport.utils.HttpUtils;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
import com.passport.zookeeper.ServiceRegistry;
=======
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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: xujianfeng
=======
/**
 * @author: Bee xu
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
 * @create: 2018-07-05 17:21
 **/
@Component
public class Connector implements InitializingBean {
<<<<<<< HEAD
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

//    启动的时候自动开始区块同步
    @EventListener(ApplicationReadyEvent.class)
    public void syncNextBlock() {
        SyncFlag.start = System.currentTimeMillis();
        //回退10个区块
        Integer lashBlockHeight = Integer.valueOf(dbAccess.getLastBlockHeight().get().toString());
        if(lashBlockHeight != 1 && lashBlockHeight > 10){
            for(int i = 0;i<10;i++){
                try {
                    dbAccess.delBlocksByHeight(new Long(lashBlockHeight-i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        //将本地受托人列表状态所有改成0
        List<Trustee> trustees = dbAccess.listTrustees();
        logger.info("初始启动受托人列表数为："+trustees.size());
        for(Trustee trustee:trustees){
            trustee.setState(0);
            dbAccess.putTrustee(trustee);
        }
        //发送同步账户请求
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
        //请求最新区块
        provider.publishEvent(new SyncNextBlockEvent(0L));
//        provider.publishEvent(new GenerateBlockEvent(0L));
    }
=======

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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
