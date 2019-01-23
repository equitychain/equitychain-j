package com.passport.listener;

<<<<<<< HEAD
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
=======
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;


@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    context = applicationContext;
  }

  public void publishEvent(ApplicationEvent event) {
    context.publishEvent(event);
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
