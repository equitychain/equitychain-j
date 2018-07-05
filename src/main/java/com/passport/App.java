package com.passport;


import com.passport.config.taskconfig.TaskThreadPoolConfig4Client;
import com.passport.config.taskconfig.TaskThreadPoolConfig4Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;


/**
 * @author Aron wu
 * TODO: 2018/6/20  BlockChain Passport implement Java
 */
@EnableAsync
@EnableConfigurationProperties({TaskThreadPoolConfig4Server.class, TaskThreadPoolConfig4Client.class}) // 开启配置属性支持
@SpringBootApplication
public class App {
  public static void main(String[] args) {
    //enable console?
    if(Arrays.stream(args).anyMatch("console"::equalsIgnoreCase)){
      System.getProperties().setProperty("spring.shell.interactive.enabled","true");
    }else{
      System.getProperties().setProperty("spring.shell.interactive.enabled","false");
    }
    SpringApplication.run(App.class, args);
  }
}
