package com.passport.db;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
<<<<<<< HEAD
public interface DataSource<K,V> {

  void put(K key,V value);
=======
public interface DataSource<K, V> {

  void put(K key, V value);
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

  V get(K key);

  void delete(K key);

  boolean flushData();

}
