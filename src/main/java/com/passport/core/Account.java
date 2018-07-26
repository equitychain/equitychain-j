package com.passport.core;

import com.passport.crypto.ECDSAUtil;

import java.math.BigDecimal;
import java.security.KeyPair;

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

    public void newAccount(){
        KeyPair keyPair = ECDSAUtil.generateKeyPair();
        this.address = ECDSAUtil.getStringFromKey(keyPair.getPublic());
        this.privateKey = ECDSAUtil.getStringFromKey(keyPair.getPrivate());
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
