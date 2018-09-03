package com.passport.service;

import java.util.List;


public interface NodesService {

  List<String> discoverNodes();

  boolean nodeReg(String str);
}
