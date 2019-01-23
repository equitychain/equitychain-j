package com.passport.service.impl;

import com.passport.annotations.RpcService;
import com.passport.service.NodesService;
<<<<<<< HEAD

import java.util.List;

/**
 * RPC接口实现
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@RpcService(NodesService.class)
public class NodesServiceImpl implements NodesService {
=======
import java.util.List;


@RpcService(NodesService.class)
public class NodesServiceImpl implements NodesService {

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  @Override
  public List<String> discoverNodes() {
    return null;
  }

  @Override
  public boolean nodeReg(String str) {
    return false;
  }
}
