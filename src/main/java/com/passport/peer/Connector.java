package com.passport.peer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@Component
public class Connector implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private ConnectAsync asyncTask;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        long begin = System.currentTimeMillis();
        asyncTask.startServer();
        asyncTask.startConnect();
        System.out.println("处理完成时间："+(System.currentTimeMillis()-begin));
    }
}
