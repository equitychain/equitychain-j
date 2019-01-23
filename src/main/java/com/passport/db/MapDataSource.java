package com.passport.db;

import java.util.Set;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
<<<<<<< HEAD
public interface MapDataSource<V> extends BatchDataSource<byte[],V> {
=======
public interface MapDataSource<V> extends BatchDataSource<byte[], V> {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

  void defaultInit();

  void specialInit(DataSourceSettings sourceSettings);

<<<<<<< HEAD
  void setDBName(String name);

  String getDBName();

=======
  String getDBName();

  void setDBName(String name);

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  boolean isAlive();

  void reset();

  void deInit();

  Set<byte[]> keySet();


<<<<<<< HEAD

=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
