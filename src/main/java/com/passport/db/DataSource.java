package com.passport.db;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public interface DataSource<K,V> {

  void put(K key,V value);

  V get(K key);

  void delete(K key);

  boolean flushData();

}
