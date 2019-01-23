package com.passport.event;

import com.passport.core.Account;
import org.springframework.context.ApplicationEvent;

/**
<<<<<<< HEAD
 * 同步账户事件
 */
public class SyncAccountEvent extends ApplicationEvent {

    public SyncAccountEvent(Account account) {
        super(account);
    }
=======
 * sync account event
 */
public class SyncAccountEvent extends ApplicationEvent {

  public SyncAccountEvent(Account account) {
    super(account);
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
