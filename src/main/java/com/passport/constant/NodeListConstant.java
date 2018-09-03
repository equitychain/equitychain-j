package com.passport.constant;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Component;


@Component
public class NodeListConstant {

  private Set<String> set = new CopyOnWriteArraySet<>();

  public boolean put(String address) {
    return set.add(address);
  }

  public boolean putAll(List<String> list) {
    return set.addAll(list);
  }

  public Set<String> getAll() {
    return set;
  }
}
