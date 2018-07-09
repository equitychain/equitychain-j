package com.passport.peer;

import com.passport.annotations.RpcService;
import com.passport.service.NodesService;
import com.passport.service.impl.RpcClient;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@Component
public class Connector implements ApplicationContextAware, InitializingBean {
    @Autowired
    private ConnectAsync asyncTask;
    @Autowired
    private RpcClient rpcClient;

    /**
     * 存放服务名称与服务实例之间的映射关系
     */
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
        asyncTask.startServer(handlerMap);
        TimeUnit.SECONDS.sleep(5);
        //asyncTask.startConnect();
        NodesService nodesService = rpcClient.create(NodesService.class);
        List<String> strings = nodesService.discoverNodes();
        System.out.println(strings);

    }
}
