package com.passport.peer;

import com.passport.annotations.RpcService;
import com.passport.constant.NodeListConstant;
import com.passport.utils.GsonUtils;
import com.passport.zookeeper.ServiceRegistry;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@Component
public class Connector implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    @Autowired
    private ConnectAsync asyncTask;
    @Autowired
    private NodeListConstant nodeListConstant;

    //存放服务名称与服务实例之间的映射关系
    private Map<String, Object> handlerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描带有 @RpcService 注解的服务类
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        //启动服务并注册到discover节点
        asyncTask.startServer();

        TimeUnit.SECONDS.sleep(3);

        //连接discover节点
        Set<String> set = nodeListConstant.getAll();
        logger.info("注册后从discover节点取到的地址列表：{}", GsonUtils.toJson(set));
        for (String address : set) {
            asyncTask.startConnect(address);
        }
    }
}
