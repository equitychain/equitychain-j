package com.passport.utils;

import com.passport.constant.Constant;

import java.math.BigDecimal;

public class RawardUtil {
    //计算区块奖励算法
    public static BigDecimal getRewardByHeight(long height){
        //那个周期
        long cycleIndex = height/Constant.cycle;
        //奖励衰减公式
        //衰减倍数
        BigDecimal factor = new BigDecimal(Math.pow(Constant.multiple,Double.parseDouble(cycleIndex+"")));
        BigDecimal reward = Constant.baseReward.multiply(factor);
        return reward;
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
