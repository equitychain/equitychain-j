package com.passport.peer;

import com.passport.constant.NodeListConstant;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.GsonUtils;
import com.passport.zookeeper.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        //启动服务并注册到discover节点
        asyncTask.startServer();

        TimeUnit.SECONDS.sleep(2);

        //连接discover节点
        Set<String> set = nodeListConstant.getAll();
        logger.info("注册后从discover节点取到的地址列表：{}", GsonUtils.toJson(set));
        String serviceAddress = InetAddress.getLocalHost().getHostAddress();
        for (String address : set) {
            if(!address.equals(serviceAddress)){
                asyncTask.startConnect(address);
            }
        }
    }

    //启动的时候自动开始区块同步
    @EventListener(ApplicationReadyEvent.class)
    public void fetchNextBlock() {
        provider.publishEvent(new SyncNextBlockEvent(0L));
    }
}
