package com.passport.utils;

import com.passport.constant.Constant;

import java.math.BigDecimal;

public class RawardUtil {
    //计算区块奖励算法
    public static BigDecimal getRewardByHeight(long height){
        Long index = height/Constant.BLOCK_DISTANCE;
        if(index > Constant.REWARD_ARRAY.length-1){
            return Constant.REWARD_ARRAY[Constant.REWARD_ARRAY.length-1];
        }else{
            return Constant.REWARD_ARRAY[index.intValue()];
        }
    }
    //校验高度的奖励
    public static boolean checkReward(long height,BigDecimal reward){
        return getRewardByHeight(height).compareTo(reward) == 0;
    }
    public static boolean checkReward(long height,String reward){
        return getRewardByHeight(height).compareTo(new BigDecimal(reward)) == 0;
    }
    public static boolean checkReward(long height,byte[] reward){
        if(reward==null || "".equals(new String(reward))) return false;
        BigDecimal rewardBalance = new BigDecimal(new String(reward));
        return getRewardByHeight(height).compareTo(rewardBalance) == 0;
    }
}
