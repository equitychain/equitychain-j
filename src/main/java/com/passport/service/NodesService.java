package com.passport.service;

import java.util.List;

/**
 * RPC接口
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
public interface NodesService {
  List<String> discoverNodes();
  boolean nodeReg(String str);
}
