package com.passport.core;

import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;

import java.math.BigDecimal;

/**
 * 受托人（候选人）
 *
 * @author: xujianfeng
 * @create: 2018-09-05 11:54
 **/
@EntityClaz(name = "trustee")
public class Trustee {
    @KeyField
    @FaildClaz(name = "address", type = String.class)
    private String address;//钱包地址
    @FaildClaz(name = "votes", type = Long.class)
    private Long votes;//得票数
    @FaildClaz(name = "generateRate", type = Float.class)
    private Float generateRate;//成功生成区块比率
    @FaildClaz(name = "income", type = BigDecimal.class)
    private BigDecimal income;//收入
    @FaildClaz(name = "status", type = Integer.class)
    private Integer status;//0撤消1正常

    public Trustee() {
    }

    public Trustee(String address, Long votes, Float generateRate, BigDecimal income, Integer status) {
        this.address = address;
        this.votes = votes;
        this.generateRate = generateRate;
        this.income = income;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
