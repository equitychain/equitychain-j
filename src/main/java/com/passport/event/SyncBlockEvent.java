package com.passport.event;

import com.passport.core.Block;
import org.springframework.context.ApplicationEvent;

<<<<<<< HEAD
/**
 * 同步区块事件
 */
public class SyncBlockEvent extends ApplicationEvent {

    public SyncBlockEvent(Block block) {
        super(block);
    }
=======

public class SyncBlockEvent extends ApplicationEvent {

  public SyncBlockEvent(Block block) {
    super(block);
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
