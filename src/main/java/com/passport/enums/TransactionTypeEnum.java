package com.passport.enums;

/**
 * 交易类型
 * @author: xujianfeng
 * @create: 2018-09-05 15:57
 **/
public enum TransactionTypeEnum {
    TRANSFER("普通转账"),
    VOTE("投票"),
    TRUSTEE_REGISTER("受托人注册"),
    TRUSTEE_CANNEL("受托人撤销注册"),
    VOTER_REGISTER("投票人注册"),
    VOTER_CANNEL("投票人撤销注册"),
    BLOCK_REWARD("出块奖励");


    TransactionTypeEnum(String desc){
        this.desc = desc;
    }
    private String desc;

    public static TransactionTypeEnum statusOf(String tradeType){
        for (TransactionTypeEnum transactionTypeEnum : TransactionTypeEnum.values()) {
            if(transactionTypeEnum.getDesc().equals(tradeType)){
                return transactionTypeEnum;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
