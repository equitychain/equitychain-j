package com.passport.event;

import org.springframework.context.ApplicationEvent;

/**
 * 同步区块事件
 */
public class SyncNextBlockEvent extends ApplicationEvent {

    public SyncNextBlockEvent(Long blockHeight) {
        super(blockHeight);
    }
}
