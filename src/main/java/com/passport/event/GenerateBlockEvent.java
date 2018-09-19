package com.passport.event;

import org.springframework.context.ApplicationEvent;

/**
 * 生成区块事件
 */
public class GenerateBlockEvent extends ApplicationEvent {

    public GenerateBlockEvent(Long blockHeight) {
        super(blockHeight);
    }
}
