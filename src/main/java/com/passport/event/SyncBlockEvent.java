package com.passport.event;

import com.passport.core.Block;
import org.springframework.context.ApplicationEvent;


public class SyncBlockEvent extends ApplicationEvent {

  public SyncBlockEvent(Block block) {
    super(block);
  }
}
