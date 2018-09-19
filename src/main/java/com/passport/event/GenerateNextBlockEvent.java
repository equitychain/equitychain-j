package com.passport.event;

import org.springframework.context.ApplicationEvent;

/**
 * 生成下一个区块事件
 */
public class GenerateNextBlockEvent extends ApplicationEvent {

    public GenerateNextBlockEvent(Long blockHeight) {
        super(blockHeight);
    }
}
