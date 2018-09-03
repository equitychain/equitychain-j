package com.passport.event;

import com.passport.core.Transaction;
import org.springframework.context.ApplicationEvent;

/**
 * send tx data
 */
public class SendTransactionEvent extends ApplicationEvent {

  public SendTransactionEvent(Transaction transaction) {
    super(transaction);
  }
}
