package com.passport.core;

import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;

/**
 * 投票记录
 * @author: xujianfeng
 * @create: 2018-09-06 11:45
 **/
@EntityClaz(name = "voteRecord")
public class VoteRecord {
    @KeyField
    @FaildClaz(name = "payAddress",type = String.class)
    private String payAddress;//投票人钱包地址
    @FaildClaz(name = "receiptAddress",type = String.class)
    private String receiptAddress;//受托人钱包地址
    @FaildClaz(name = "voteNum",type = Integer.class)
    private Integer voteNum;//投票数,默认一次只可投一票
    @FaildClaz(name = "status",type = Integer.class)
    private Integer status;//0撤消1正常
    @FaildClaz(name = "time",type = Long.class)
    private Long time;//时间戳
    public VoteRecord(){

    }
    public VoteRecord(String payAddress, String receiptAddress, Integer voteNum, Integer status){
        this.payAddress = payAddress;
        this.receiptAddress = receiptAddress;
        this.voteNum = voteNum;
        this.status = status;
    }

    public String getPayAddress() {
        return payAddress;
    }

    public void setPayAddress(String payAddress) {
        this.payAddress = payAddress;
    }

    public String getReceiptAddress() {
        return receiptAddress;
    }

    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
    }

    public Integer getVoteNum() {
        return voteNum;
    }

    public void setVoteNum(Integer voteNum) {
        this.voteNum = voteNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
