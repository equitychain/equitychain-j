package com.passport.service.impl;

import com.passport.annotations.RpcService;
import com.passport.service.NodesService;

import java.util.List;

/**
 * RPC接口实现
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@RpcService(NodesService.class)
public class NodesServiceImpl implements NodesService {
  @Override
  public List<String> discoverNodes() {
    return null;
  }
}
