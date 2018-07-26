package com.passport.event;

import org.springframework.context.ApplicationEvent;

/**
 * 同步账户事件
 */
public class SyncAccountEvent extends ApplicationEvent {

    public SyncAccountEvent(Long blockHeight) {
        super(blockHeight);
    }
}
