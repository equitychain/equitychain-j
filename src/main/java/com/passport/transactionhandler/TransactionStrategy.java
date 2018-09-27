package com.passport.transactionhandler;

import com.passport.core.Transaction;
import com.passport.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: xujianfeng
 * @create: 2018-09-10 17:39
 **/
@Component
public abstract class TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TransactionStrategy.class);

    public void handleTransaction(Transaction transaction){
        if(CheckUtils.checkTransaction(transaction)){
            handle(transaction);
        }
    }

    protected abstract void handle(Transaction transaction);
}
