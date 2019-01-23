package com.passport.event;

import org.springframework.context.ApplicationEvent;

<<<<<<< HEAD
/**
 * 同步区块事件
 */
public class SyncNextBlockEvent extends ApplicationEvent {

    public SyncNextBlockEvent(Long blockHeight) {
        super(blockHeight);
    }
=======

public class SyncNextBlockEvent extends ApplicationEvent {

  public SyncNextBlockEvent(Long blockHeight) {
    super(blockHeight);
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
