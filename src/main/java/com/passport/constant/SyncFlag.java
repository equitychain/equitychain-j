package com.passport.constant;

import com.passport.core.Trustee;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 组1主动同步区块事件和组2接收广播区块、接收广播流水
 * @author: xujianfeng
 * @create: 2018-08-16 15:56
 **/
public class SyncFlag {
    private static volatile boolean nextBlockSyncFlag = true;//默认处理发布主动同步区块事件，为false则处理组2接收广播区块、接收广播流水

    public static boolean isNextBlockSyncFlag() {
        return nextBlockSyncFlag;
    }

    public static void setNextBlockSyncFlag(boolean nextBlockSyncFlag) {
        SyncFlag.nextBlockSyncFlag = nextBlockSyncFlag;
    }
    public static Long blockHeight = 1L;//进度条使用轮训初始高度

    public static boolean blockTimeFlag = false;//是否启动定时任务

    public static boolean minerFlag = true;//允许出块

    public static ConcurrentMap<String,Integer> waitMiner = new ConcurrentHashMap<>();//启动挖矿账户缓存

    public static ConcurrentMap<String,List<Trustee>> blockCycleList = new ConcurrentHashMap<>();//周期

    public static ConcurrentMap<String,Boolean> keystoreAddressStatus = new ConcurrentHashMap<>();//keystore里账户是否启动了挖矿状态

    public static Long start = 0L;

    public static Long end = 0L;

}
