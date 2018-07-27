package com.passport.event;

import com.passport.core.Account;
import org.springframework.context.ApplicationEvent;

/**
 * 同步账户事件
 */
public class SyncAccountEvent extends ApplicationEvent {

    public SyncAccountEvent(Account account) {
        super(account);
    }
}
