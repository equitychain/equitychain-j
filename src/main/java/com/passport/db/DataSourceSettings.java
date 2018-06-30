package com.passport.db;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public class DataSourceSettings {

  private int maxOpenFiles = 32;
  private int maxThreads = 1;

  private static DataSourceSettings instance = null;

  private DataSourceSettings(){}

  public static synchronized DataSourceSettings getInstance(){
    if (instance == null){
      instance = new DataSourceSettings();
    }
    return instance;
  }

  public int getMaxOpenFiles() {
    return maxOpenFiles;
  }

  public void setMaxOpenFiles(int maxOpenFiles) {
    this.maxOpenFiles = maxOpenFiles;
  }

  public int getMaxThreads() {
    return maxThreads;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }
}
