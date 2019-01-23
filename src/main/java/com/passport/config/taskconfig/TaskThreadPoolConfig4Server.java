package com.passport.config.taskconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

<<<<<<< HEAD
/**
 * @author: xujianfeng
 * @create: 2018-04-20 23:26
 **/
@ConfigurationProperties(prefix = "serverexecutepool") // 该注解的locations已经被启用，现在只要是在环境中，都会优先加载
public class TaskThreadPoolConfig4Server {
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveSeconds;
    private int queueCapacity;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
=======

@ConfigurationProperties(prefix = "serverexecutepool")
public class TaskThreadPoolConfig4Server {

  private int corePoolSize;
  private int maxPoolSize;
  private int keepAliveSeconds;
  private int queueCapacity;

  public int getCorePoolSize() {
    return corePoolSize;
  }

  public void setCorePoolSize(int corePoolSize) {
    this.corePoolSize = corePoolSize;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

  public int getKeepAliveSeconds() {
    return keepAliveSeconds;
  }

  public void setKeepAliveSeconds(int keepAliveSeconds) {
    this.keepAliveSeconds = keepAliveSeconds;
  }

  public int getQueueCapacity() {
    return queueCapacity;
  }

  public void setQueueCapacity(int queueCapacity) {
    this.queueCapacity = queueCapacity;
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
