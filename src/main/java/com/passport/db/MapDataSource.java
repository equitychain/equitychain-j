package com.passport.db;

import java.util.Set;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public interface MapDataSource<V> extends BatchDataSource<byte[], V> {

  void defaultInit();

  void specialInit(DataSourceSettings sourceSettings);

  String getDBName();

  void setDBName(String name);

  boolean isAlive();

  void reset();

  void deInit();

  Set<byte[]> keySet();


}
