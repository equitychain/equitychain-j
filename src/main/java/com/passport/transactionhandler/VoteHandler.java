package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.db.dbhelper.DBAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 普通用户投票
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("VOTE")
public class VoteHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(VoteHandler.class);

    @Autowired
    private DBAccess dbAccess;

    @Override
    protected void handle(Transaction transaction) {
        String payAddress = new String(transaction.getPayAddress());
        String receiptAddress = new String(transaction.getReceiptAddress());
        //投票人是否有资格投票
        Optional<Voter> voterOptional = dbAccess.getVoter(payAddress);
        if(!voterOptional.isPresent() || voterOptional.get().getStatus() == 0){
            return;
        }
        Optional<Account> accountOptional = dbAccess.getAccount(payAddress);
        if(!accountOptional.isPresent()){
            return;
        }
        Account account = accountOptional.get();
        Voter voter = voterOptional.get();
        if(voter.getVoteNum() < 1){
            return;
        }

        //受托人是否合法
        Optional<Trustee> trusteeOptional = dbAccess.getTrustee(receiptAddress);
        if(!trusteeOptional.isPresent() || trusteeOptional.get().getStatus() == 0){
            return;
        }
        Trustee trustee = trusteeOptional.get();

        //增加投票记录
        dbAccess.putVoteRecord(new VoteRecord(payAddress, receiptAddress, 1, 1));

        //投票人减少持票
        voter.setVoteNum(voter.getVoteNum()-1);
        //受托人增加持票
        trustee.setVotes(trustee.getVotes()+1);
        //扣除矿工费
        account.setBalance(account.getBalance().subtract(getFee(transaction)));
        if(account.getBalance().compareTo(BigDecimal.ZERO)<0){
            return;
        }
        dbAccess.putAccount(account);
        dbAccess.putVoter(voter);
        dbAccess.putTrustee(trustee);
    }
}
