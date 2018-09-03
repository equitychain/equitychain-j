package com.passport.db;

import java.util.Map;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public interface BatchDataSource<K, V> extends DataSource<K, V> {

  void batchUpdate(Map<K, V> datas);

}
