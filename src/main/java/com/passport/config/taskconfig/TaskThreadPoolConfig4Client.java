package com.passport.config.taskconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "clientexecutepool")
public class TaskThreadPoolConfig4Client {

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
}
