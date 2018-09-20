package com.passport.core;

import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;

import java.math.BigDecimal;

/**
 * 账户
 * @author: xujianfeng
 * @create: 2018-07-26 11:20
 **/
@EntityClaz(name = "account")
public class Account{
    @KeyField
    @FaildClaz(name = "address",type = String.class)
    private String address;//地址
    @FaildClaz(name = "privateKey",type = String.class)
    private String privateKey;//私钥
    @FaildClaz(name = "balance",type = BigDecimal.class)
    private BigDecimal balance;//余额
    @FaildClaz(name = "password",type = String.class)
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

    @Override
    public String toString() {
        return "Account{" +
                "address='" + address + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", balance=" + balance +
                ", password='" + password + '\'' +
                '}';
    }

    public boolean isNullContent() {
        return privateKey == null && balance == null && password == null;
    }
}
