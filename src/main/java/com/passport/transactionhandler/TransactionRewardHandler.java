package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.CastUtils;
import com.passport.webhandler.BlockHandler;
import com.passport.webhandler.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 确认流水奖励
 *
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("CONFIRM_REWARD")
public class TransactionRewardHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRewardHandler.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private BlockHandler blockHandler;
    @Autowired
    private TransactionHandler transactionHandler;

    @Override
    protected void handle(Transaction transaction) {
        byte[] payAddressByte = transaction.getPayAddress();
        // 确认流水奖励
        if (payAddressByte == null) {
            Optional<Account> accountOptional = dbAccess.getAccount(new String(transaction.getReceiptAddress()));
            if (accountOptional.isPresent()) {
                BigDecimal reward = new BigDecimal(new String(transaction.getValue()));
                BigDecimal valueBigDecimal = transactionHandler.getTempEggByHash(transaction.getHash());
                if (valueBigDecimal == null || reward.compareTo(valueBigDecimal) != 0) {//校验奖励金额未通过
                    return;
                }

                Account account = accountOptional.get();
                account.setBalance(account.getBalance().add(valueBigDecimal));
                dbAccess.putAccount(account);
            }
        }
    }
}
