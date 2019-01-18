package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncAccountEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.CastUtils;
import com.passport.utils.RawardUtil;
import com.passport.webhandler.BlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 区块奖励
 *
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("TOKEN")
public class TokenHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TokenHandler.class);

    @Autowired
    private DBAccess dbAccess;
    //广播event用的
    @Autowired
    private ApplicationContextProvider provider;

    @Override
    protected void handle(Transaction transaction) {
        byte[] payAddressByte = transaction.getPayAddress();
        Optional<Account> accountOptional = dbAccess.getAccount(new String(payAddressByte)+"_"+ Constant.MAIN_COIN);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setBalance(account.getBalance().subtract(new BigDecimal(1000)));
            dbAccess.putAccount(account);
            //更新代币
            String[] addressToken = account.getAddress_token().split("_");
            account.setAddress_token(addressToken[0]+new String(transaction.getToken()));
            account.setBalance(new BigDecimal(new String(transaction.getValue())));
            dbAccess.putAccount(account);
        }

    }
}
