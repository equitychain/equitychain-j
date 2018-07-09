package com.passport.config.zkconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xujianfeng
 * @create: 2018-04-20 23:26
 **/
@ConfigurationProperties(prefix = "rpc") // 该注解的locations已经被启用，现在只要是在环境中，都会优先加载
public class ZooKeeperConfig {
    private String zkRegistryAddress;
    private int zkSessionTimeout;
    private int zkConnectionTimeout;
    private String zkRegistryPath;

    public String getZkRegistryAddress() {
        return zkRegistryAddress;
    }

    public void setZkRegistryAddress(String zkRegistryAddress) {
        this.zkRegistryAddress = zkRegistryAddress;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public void setZkConnectionTimeout(int zkConnectionTimeout) {
        this.zkConnectionTimeout = zkConnectionTimeout;
    }

    public String getZkRegistryPath() {
        return zkRegistryPath;
    }

    public void setZkRegistryPath(String zkRegistryPath) {
        this.zkRegistryPath = zkRegistryPath;
    }
}
