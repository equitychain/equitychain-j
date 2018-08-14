package com.passport.timer;

import com.passport.peer.ConnectAsync;
import com.passport.utils.HttpUtils;
import com.passport.zookeeper.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 节点定时器
 */
@Component
public class NodeTimers {
    @Autowired
    ServiceRegistry registry;
    @Autowired
    private ConnectAsync asyncTask;
    Logger logger = LoggerFactory.getLogger(NodeTimers.class);
    /**
     * 发现新节点的定时器
     */
    //@Scheduled(cron = "0/10 * * * * ?")
    public void findNodeList(){
        try {
            String ip = HttpUtils.getLocalHostLANAddress().getHostAddress();
            List<String> newNodeIp = registry.findNode(ip);
            logger.info("new node list size:"+newNodeIp.size());
            for (String address : newNodeIp) {
                if(!address.equals(ip)){
                    asyncTask.startConnect(address);
                    logger.info("new node ip:"+address);
                }
            }
        }catch (Exception e){

        }
    }
}