package com.passport.config.taskconfig;

<<<<<<< HEAD
=======
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: xujianfeng
=======
/**
 * @author: Bee xu
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
 * @create: 2018-04-20 23:29
 **/
@Component
public class TaskThreadPool4Client {
<<<<<<< HEAD
    @Autowired
    private TaskThreadPoolConfig4Client config;

    //自定义线程池
    @Bean
    public Executor taskAsyncPool4Client() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        executor.setThreadNamePrefix("MyExecutor-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
=======

  @Autowired
  private TaskThreadPoolConfig4Client config;


  @Bean
  public Executor taskAsyncPool4Client() {
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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}

