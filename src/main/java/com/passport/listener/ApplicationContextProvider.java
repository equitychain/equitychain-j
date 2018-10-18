package com.passport.listener;

import com.passport.config.taskconfig.EventThreadPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * spring上下文管理器
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    public static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }

    @Async("asyncServiceExecutor")
    public void publishEvent(ApplicationEvent event) {
        try {
            context.publishEvent(event);
//            System.out.println("1231231231231=-==-=-=-=");
//            new PublishThread(context, event).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class PublishThread extends Thread {
    ApplicationContext context;
    ApplicationEvent event;

    public PublishThread(ApplicationContext context, ApplicationEvent event) {
        this.context = context;
        this.event = event;
    }

    @Override
    public void run() {
//            Thread.sleep(2000);
        context.publishEvent(event);
    }
}
