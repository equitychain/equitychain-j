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
    public static Long blockHeight = 0L;
    public static boolean isNextBlockSyncFlag() {
        return nextBlockSyncFlag;
    }
    public static void setNextBlockSyncFlag(boolean nextBlockSyncFlag) {
        SyncFlag.nextBlockSyncFlag = nextBlockSyncFlag;
    }
    public static boolean blockTimeFlag = false;//是否能启动出块 只允许一次

    public static boolean minerFlag = true;//允许出块

    public static ConcurrentMap<String,Integer> waitMiner = new ConcurrentHashMap<>();

    public static ConcurrentMap<String,List<Trustee>> blockCycleList = new ConcurrentHashMap<>();

}
