package com.passport.core;

/**
 * 投票人
 * @author: xujianfeng
 * @create: 2018-09-06 11:45
 **/
public class Voter {
    private String address;//钱包地址
    private Integer voteNum;//可用投票数
    private Integer status;//0撤消1正常
    public Voter(){

    }
    public Voter(String address, Integer voteNum, Integer status){
        this.address = address;
        this.voteNum = voteNum;
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
