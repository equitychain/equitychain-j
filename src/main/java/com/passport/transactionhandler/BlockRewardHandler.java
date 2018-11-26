package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
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
@Component("BLOCK_REWARD")
public class BlockRewardHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(BlockRewardHandler.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private BlockHandler blockHandler;

    @Override
    protected void handle(Transaction transaction) {
        byte[] payAddressByte = transaction.getPayAddress();
        //挖矿奖励 TODO 出块同时奖励投票人
        if (payAddressByte == null || payAddressByte.length == 0) {
            Optional<Account> accountOptional = dbAccess.getAccount(new String(transaction.getReceiptAddress())+"_"+ Constant.MAIN_COIN);
            if (accountOptional.isPresent()) {
                String blockHeight = new String(transaction.getBlockHeight());
                BigDecimal reward = RawardUtil.getRewardByHeight(CastUtils.castLong(blockHeight));
                BigDecimal valueBigDecimal = CastUtils.castBigDecimal(new String(transaction.getValue()));//交易金额
                if (reward.compareTo(valueBigDecimal) != 0) {//出块奖励
                    return;
                }

                Account account = accountOptional.get();
                account.setBalance(account.getBalance().add(valueBigDecimal));
                dbAccess.putAccount(account);
            }
        }
    }
}
