package com.passport.constant;

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
    public static boolean blockSyncFlag = false;//是否在定时任务启动重选受托人

    public static ConcurrentMap<String,Integer> waitMiner = new ConcurrentHashMap<>();
}
