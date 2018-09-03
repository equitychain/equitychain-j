package com.passport.config.taskconfig;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


@Component
public class TaskThreadPool4Server {

  @Autowired
  private TaskThreadPoolConfig4Server config;


  @Bean
  public Executor taskAsyncPool4Server() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(config.getCorePoolSize());
    executor.setMaxPoolSize(config.getMaxPoolSize());
    executor.setQueueCapacity(config.getQueueCapacity());
    executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
    executor.setThreadNamePrefix("MyExecutor-");

    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}

