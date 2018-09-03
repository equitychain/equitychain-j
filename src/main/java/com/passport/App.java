package com.passport;


import com.passport.config.taskconfig.TaskThreadPoolConfig4Client;
import com.passport.config.taskconfig.TaskThreadPoolConfig4Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Aron wu TODO: 2018/6/20  BlockChain Passport implement Java
 */
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({TaskThreadPoolConfig4Server.class,
    TaskThreadPoolConfig4Client.class})
@SpringBootApplication(scanBasePackages = {"com.passport"})
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
