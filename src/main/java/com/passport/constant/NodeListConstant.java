package com.passport.constant;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 存储节点地址
 * @author: xujianfeng
 * @create: 2018-07-10 17:43
 **/
@Component
public class NodeListConstant {
    private Set<String> set = new CopyOnWriteArraySet<>();

    public boolean put(String address){
        return set.add(address);
    }

    public boolean putAll(List<String> list){
        return set.addAll(list);
    }

    public Set<String> getAll(){
        return set;
    }
}
