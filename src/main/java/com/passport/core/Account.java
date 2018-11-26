package com.passport.core;

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

    private String address;

    private String token;

    public Account() {

    }

    public Account(String address_token, BigDecimal balance) {
        this.address_token = address_token;
        this.balance = balance;
    }

    public Account(String address_token, String privateKey, BigDecimal balance,String address,String token) {
        this.address_token = address_token;
        this.privateKey = privateKey;
        this.balance = balance;
        this.address = address;
        this.token = token;
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

    @Override
    public String toString() {
        return "Account{" +
                "address_token='" + address_token + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", balance=" + balance +
                ", password='" + password + '\'' +
                '}';
    }

    public boolean isNullContent() {
        return privateKey == null && balance == null && password == null;
    }
}
