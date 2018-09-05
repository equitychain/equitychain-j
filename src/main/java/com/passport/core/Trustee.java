package com.passport.core;

import java.math.BigDecimal;

/**
 * 受托人（候选人）
 * @author: xujianfeng
 * @create: 2018-09-05 11:54
 **/
public class Trustee {
    private String address;//钱包地址
    private Long votes;//得票数
    private Float generateRate;//成功生成区块比率
    private BigDecimal income;//收入

    public Trustee() {
    }

    public Trustee(String address, Long votes, Float generateRate, BigDecimal income) {
        this.address = address;
        this.votes = votes;
        this.generateRate = generateRate;
        this.income = income;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public Float getGenerateRate() {
        return generateRate;
    }

    public void setGenerateRate(Float generateRate) {
        this.generateRate = generateRate;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
