package com.passport.core;

/**
 * 投票记录
 * @author: xujianfeng
 * @create: 2018-09-06 11:45
 **/
public class VoteRecord {
    private String payAddress;//投票人钱包地址
    private String receiptAddress;//受托人钱包地址
    private Integer voteNum;//投票数,默认一次只可投一票
    private Integer status;//0撤消1正常
    private Long timestamp;//投票时间
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
}
