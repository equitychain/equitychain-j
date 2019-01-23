package com.passport.db;

<<<<<<< HEAD
import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
=======

import java.io.IOException;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4


/**
 * @author Wu Created by SKINK on 2018/6/27.
 */
public class RocksDBTest {


<<<<<<< HEAD

=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  /*
  * rocksdb implement
  *
  * **/
<<<<<<< HEAD
  public static void main(String[] args) throws Exception {

    List<byte[]> curColumns = RocksDB.listColumnFamilies(new Options(), "./passportjDir");
    List<String> curColumnsStr = new ArrayList<>();
    for (byte[] curColumn : curColumns) {
      curColumnsStr.add(new String(curColumn));
    }
    System.out.println(curColumnsStr.contains("trusteeVotes-index"));
=======
  public static void main(String[] args) throws IOException {

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  }

}
