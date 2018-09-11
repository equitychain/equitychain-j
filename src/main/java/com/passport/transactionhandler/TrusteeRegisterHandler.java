package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.DBAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 处理委托人注册
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("TRUSTEE_REGISTER")
public class TrusteeRegisterHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeRegisterHandler.class);

    @Autowired
    private DBAccess dbAccess;

    @Override
    protected void handle(Transaction transaction) {
        String payAddress = new String(transaction.getPayAddress());
        Optional<Trustee> trusteeOptional = dbAccess.getTrustee(payAddress);
        if (!trusteeOptional.isPresent() || trusteeOptional.get().getStatus() == 0) {//已是委托人则不能重复注册
            //判断受托人资产是否足够
            Optional<Account> accountOptional = dbAccess.getAccount(payAddress);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                BigDecimal balance = account.getBalance();
                BigDecimal result = balance.subtract(Constant.FEE_4_REGISTER_TRUSTEE);
                if (result.compareTo(new BigDecimal(0)) != -1) {
                    //加入到委托人列表中，如之前存在则覆盖原来记录
                    boolean flag = dbAccess.putTrustee(new Trustee(payAddress, 0L, 0F, new BigDecimal(0), 1));
                    if (flag) {
                        account.setBalance(result);
                        dbAccess.putAccount(account);
                    }
                }
            }
        }
    }
}
