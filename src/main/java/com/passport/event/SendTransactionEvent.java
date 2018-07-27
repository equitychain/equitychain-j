package com.passport.event;

import com.passport.core.Transaction;
import org.springframework.context.ApplicationEvent;

/**
 * 发送交易数据
 */
public class SendTransactionEvent extends ApplicationEvent {

    public SendTransactionEvent(Transaction transaction) {
        super(transaction);
    }
}
