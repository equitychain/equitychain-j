package com.passport.event;

import com.passport.core.Block;
import org.springframework.context.ApplicationEvent;

/**
 * 同步区块事件
 */
public class SyncBlockEvent extends ApplicationEvent {

    public SyncBlockEvent(Block block) {
        super(block);
    }
}
