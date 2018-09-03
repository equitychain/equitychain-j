package com.passport.event;

import org.springframework.context.ApplicationEvent;


public class SyncNextBlockEvent extends ApplicationEvent {

  public SyncNextBlockEvent(Long blockHeight) {
    super(blockHeight);
  }
}
