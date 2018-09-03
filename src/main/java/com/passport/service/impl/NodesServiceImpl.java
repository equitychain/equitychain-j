package com.passport.service.impl;

import com.passport.annotations.RpcService;
import com.passport.service.NodesService;
import java.util.List;


@RpcService(NodesService.class)
public class NodesServiceImpl implements NodesService {

  @Override
  public List<String> discoverNodes() {
    return null;
  }

  @Override
  public boolean nodeReg(String str) {
    return false;
  }
}
