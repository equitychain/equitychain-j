package com.passport.db;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Wu Created by SKINK on 2018/6/27.
 */
public class RocksDBTest {



  /*
  * rocksdb implement
  *
  * **/
  public static void main(String[] args) throws Exception {

    List<byte[]> curColumns = RocksDB.listColumnFamilies(new Options(), "./passportjDir");
    List<String> curColumnsStr = new ArrayList<>();
    for (byte[] curColumn : curColumns) {
      curColumnsStr.add(new String(curColumn));
    }
    System.out.println(curColumnsStr.contains("trusteeVotes-index"));
  }

}
