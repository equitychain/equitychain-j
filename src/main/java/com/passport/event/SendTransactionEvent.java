package com.passport.event;

import com.passport.core.Transaction;
import org.springframework.context.ApplicationEvent;

/**
<<<<<<< HEAD
 * 发送交易数据
 */
public class SendTransactionEvent extends ApplicationEvent {

    public SendTransactionEvent(Transaction transaction) {
        super(transaction);
    }
=======
 * send tx data
 */
public class SendTransactionEvent extends ApplicationEvent {

  public SendTransactionEvent(Transaction transaction) {
    super(transaction);
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
