package com.passport.db;

import java.util.Set;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public interface MapDataSource<V> extends BatchDataSource<byte[],V> {

  void defaultInit();

  void specialInit(DataSourceSettings sourceSettings);

  void setDBName(String name);

  String getDBName();

  boolean isAlive();

  void reset();

  void deInit();

  Set<byte[]> keySet();



}
