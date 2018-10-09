package com.passport.listener;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * spring上下文管理器
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
    @Async
    public void publishEvent(ApplicationEvent event) {
        try{
            Thread.sleep(2000);
            context.publishEvent(event);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
