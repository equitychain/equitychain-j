package com.passport;


import com.passport.config.taskconfig.TaskThreadPoolConfig4Client;
import com.passport.config.taskconfig.TaskThreadPoolConfig4Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;


/**
 * @author Aron wu
 * TODO: 2018/6/20  BlockChain Passport implement Java
 */
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({TaskThreadPoolConfig4Server.class, TaskThreadPoolConfig4Client.class}) // 开启配置属性支持
@SpringBootApplication(scanBasePackages = {"com.passport"})
public class App {
   public static void main(String[] args) {
//    ApplicationContext abstractApplicationContext = SpringApplication.run(App.class, args);
    SpringApplication.run(App.class, args);

//       try {
//           Enumeration<URL> ss =  abstractApplicationContext.getClassLoader().getResources("com.passport.core");
//           while (ss.hasMoreElements()) {
//               // 获取下一个元素
//               URL url = ss.nextElement();
//               System.out.println("APP:"+url);
//           }
//       } catch (IOException e) {
//           e.printStackTrace();
//       }

   }
}
