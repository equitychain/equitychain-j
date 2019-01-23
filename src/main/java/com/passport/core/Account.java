package com.passport.core;

<<<<<<< HEAD
import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import com.passport.constant.Constant;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 账户
 * @author: xujianfeng
 * @create: 2018-07-26 11:20
 **/
@EntityClaz(name = "account")
public class Account{
    @KeyField
    @FaildClaz(name = "address_token",type = String.class)
    private String address_token;//地址
    @FaildClaz(name = "privateKey",type = String.class)
    private String privateKey;//私钥
    @FaildClaz(name = "balance",type = BigDecimal.class)
    private BigDecimal balance;//余额
    @FaildClaz(name = "password",type = String.class)
    private String password;//交易密码
    @FaildClaz(name = "identity",type = String.class)
    private String identity;//交易密码

    private String address;

    private String token;

    private String mnemonic;

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public Account() {

    }

    public Account(String address_token, BigDecimal balance) {
        this.address_token = address_token;
        this.balance = balance;
    }

    public Account(String address_token, String privateKey, BigDecimal balance,String address,String token,String identity) {
        this.address_token = address_token;
        this.privateKey = privateKey;
        this.balance = balance;
        this.address = address;
        this.token = token;
        this.identity = identity;
    }

    public String getAddress_token() {
        return address_token;
    }

    public void setAddress_token(String address_token) {
        this.address_token = address_token;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "Account{" +
                "address_token='" + address_token + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", balance=" + balance +
                ", password='" + password + '\'' +
                ", identity='" + identity + '\'' +
                '}';
    }

    public boolean isNullContent() {
        return privateKey == null && balance == null && password == null;
    }
=======
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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
