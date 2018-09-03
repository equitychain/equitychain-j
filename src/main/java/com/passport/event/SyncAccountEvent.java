package com.passport.event;

import com.passport.core.Account;
import org.springframework.context.ApplicationEvent;

/**
 * sync account event
 */
public class SyncAccountEvent extends ApplicationEvent {

  public SyncAccountEvent(Account account) {
    super(account);
  }
}
