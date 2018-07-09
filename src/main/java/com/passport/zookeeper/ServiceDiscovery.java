package com.passport.zookeeper;

import com.passport.config.zkconfig.ZooKeeperConfig;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务发现
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@Component
public class ServiceDiscovery {
  private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

  @Autowired
  private ZooKeeperConfig zooKeeperConfig;

  public String discover(String name) {
    //创建ZooKeeper客户端
    ZkClient zkClient = new ZkClient(zooKeeperConfig.getZkRegistryAddress(), zooKeeperConfig.getZkSessionTimeout(), zooKeeperConfig.getZkConnectionTimeout());
    logger.debug("连接zookeeper成功");
    try {
      //获取service节点
      String servicePath = zooKeeperConfig.getZkRegistryPath() + "/" + name;
      if (!zkClient.exists(servicePath)) {
        throw new RuntimeException(String.format("不存在如下service节点: %s", servicePath));
      }
      List<String> addressList = zkClient.getChildren(servicePath);
      if (CollectionUtils.isEmpty(addressList)) {
        throw new RuntimeException(String.format("不存在如下address节点: %s", servicePath));
      }
      //获取address节点
      String address;
      int size = addressList.size();
      if (size == 1) {
        //若只有一个地址，则获取该地址
        address = addressList.get(0);
        logger.debug("只有一个address节点: {}", address);
      } else {
        // 若存在多个地址，则随机获取一个地址
        address = addressList.get(ThreadLocalRandom.current().nextInt(size));
        logger.debug("随机取一个address节点: {}", address);
      }
      //获取address节点的值
      String addressPath = servicePath + "/" + address;
      return zkClient.readData(addressPath);
    } finally {
      zkClient.close();
    }
  }
}