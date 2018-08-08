package com.passport.core;

import java.math.BigDecimal;

/**
 * 账户
 * @author: xujianfeng
 * @create: 2018-07-26 11:20
 **/
public class Account {
    private String address;//地址
    private String privateKey;//私钥
    private BigDecimal balance;//余额
    private String password;//交易密码

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
