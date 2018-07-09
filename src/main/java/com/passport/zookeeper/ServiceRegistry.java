package com.passport.zookeeper;

import com.passport.config.zkconfig.ZooKeeperConfig;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 服务注册
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@Component
public class ServiceRegistry {
  private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

  @Autowired
  private ZooKeeperConfig zooKeeperConfig;

  private ZkClient zkClient;

  @PostConstruct
  public void init() {
    //创建ZooKeeper客户端
    zkClient = new ZkClient(zooKeeperConfig.getZkRegistryAddress(), zooKeeperConfig.getZkSessionTimeout(), zooKeeperConfig.getZkConnectionTimeout());
    logger.debug("连接zookeeper成功");
  }

  public void register(String serviceName, String serviceAddress) {
    //创建registry节点（持久）
    String zkRegistryPath = zooKeeperConfig.getZkRegistryPath();
    if (!zkClient.exists(zkRegistryPath)) {
      zkClient.createPersistent(zkRegistryPath);
      logger.debug("创建注册节点: {}", zkRegistryPath);
    }
    //创建service节点（持久）
    String servicePath = zkRegistryPath + "/" + serviceName;
    if (!zkClient.exists(servicePath)) {
      zkClient.createPersistent(servicePath);
      logger.debug("创建service节点: {}", servicePath);
    }
    //创建address节点（临时）
    String addressPath = servicePath + "/address-";
    String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
    logger.debug("创建address节点: {}", addressNode);
  }
}