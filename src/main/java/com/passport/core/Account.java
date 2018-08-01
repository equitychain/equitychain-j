package com.passport.core;

import com.passport.crypto.eth.ECKeyPair;

import java.math.BigDecimal;

/**
 * 账户
 * @author: xujianfeng
 * @create: 2018-07-26 11:20
 **/
public class Account {
    private String address;//公钥
    private String privateKey;//私钥
    private BigDecimal balance;//余额

    public Account() {

    }

    public Account(String address, String privateKey, BigDecimal balance) {
        this.address = address;
        this.privateKey = privateKey;
        this.balance = balance;
    }

    public void newAccount(ECKeyPair keyPair){


        balance = BigDecimal.ZERO;
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
}
