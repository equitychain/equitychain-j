package com.passport.core;

import java.math.BigDecimal;

/**
 * @author: Bee xu
 * @create: 2018-07-26 11:20
 **/
public class Account {

  private String address;
  private String privateKey;
  private BigDecimal balance;
  private String password;

  public Account() {

  }

  public Account(String address, BigDecimal balance) {
    this.address = address;
    this.balance = balance;
  }

  public Account(String address, String privateKey, BigDecimal balance) {
    this.address = address;
    this.privateKey = privateKey;
    this.balance = balance;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
