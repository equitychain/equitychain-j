package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.CastUtils;
import com.passport.webhandler.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 普通转账
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("TRANSFER")
public class TransferHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TransferHandler.class);

    @Autowired
    private DBAccess dbAccess;

    @Override
    protected void handle(Transaction transaction) {
        String payAddress = new String(transaction.getPayAddress());
        String receiptAddress = new String(transaction.getReceiptAddress());
        String token = new String(transaction.getToken());

        Optional<Account> payAddressOptional = dbAccess.getAccount(payAddress+"_"+token);
        if(!payAddressOptional.isPresent()){
            return;
        }

        Optional<Account> receiptAddressOptional = dbAccess.getAccount(receiptAddress+"_"+token);
        if(!receiptAddressOptional.isPresent()){
            return;
        }

        BigDecimal valueBigDecimal = CastUtils.castBigDecimal(new String(transaction.getValue()));//交易金额
        Account payAddressAccount = payAddressOptional.get();
        BigDecimal valueDec = getFee(transaction);
        BigDecimal result = payAddressAccount.getBalance().subtract(valueBigDecimal.add(valueDec));
        if(result.compareTo(new BigDecimal(0)) == -1){//余额不足
            return;
        }

        //转出账户减少余额，收款账户增加余额
        Account receiptAddressAccount = receiptAddressOptional.get();
        payAddressAccount.setBalance(result);
        receiptAddressAccount.setBalance(receiptAddressAccount.getBalance().add(valueBigDecimal));
        dbAccess.putAccount(payAddressAccount);
        dbAccess.putAccount(receiptAddressAccount);
    }
}
