package com.passport.constant;

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
    public static volatile boolean blockFlag = false;//是否启动定时任务
    public static volatile boolean blockSyncFlag = false;//是否在定时任务启动重选受托人
}
