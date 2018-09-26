package com.passport.core;

import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;

/**
 * 投票人
 * @author: xujianfeng
 * @create: 2018-09-06 11:45
 **/
@EntityClaz(name = "voter")
public class Voter {
    @KeyField
    @FaildClaz(name = "address",type = String.class)
    private String address;//钱包地址
    @FaildClaz(name = "voteNum",type = Integer.class)
    private Integer voteNum;//可用投票数
    @FaildClaz(name = "status",type = Integer.class)
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

    @Override
    public int hashCode() {
        return status;
    }
    public boolean isNullContent(){
        return voteNum == null && status == null;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Voter){
            Voter v = (Voter) obj;
            if(address == null || "".equals(address)){
                return (v.address == null || "".equals(v.address));
            }else{
                return address.equals(v.address);
            }
        }
        return false;
    }
}
