package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.core.Voter;
import com.passport.db.dbhelper.DBAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 处理投票人注册
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("VOTER_REGISTER")
public class VoterRegisterHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(VoterRegisterHandler.class);

    @Autowired
    private DBAccess dbAccess;

    @Override
    protected void handle(Transaction transaction) {
        String payAddress = new String(transaction.getPayAddress());
        Optional<Voter> voterOptional = dbAccess.getVoter(payAddress);
        if (!voterOptional.isPresent() || voterOptional.get().getStatus() == 0) {//已是投票人则不能重复注册
            //判断投票人资产是否足够
            Optional<Account> accountOptional = dbAccess.getAccount(payAddress);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                BigDecimal balance = account.getBalance();
                BigDecimal result = balance.subtract(Constant.FEE_4_REGISTER_VOTER);
                if (result.compareTo(new BigDecimal(0)) != -1) {
                    //加入到投票人列表中，如之前存在则覆盖原来记录
                    boolean flag = dbAccess.putVoter(new Voter(payAddress, Constant.CHANCE_4_VOTER, 1));
                    if (flag) {
                        account.setBalance(result);
                        dbAccess.putAccount(account);
                    }
                }
            }
        }
    }
}
