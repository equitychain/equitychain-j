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
import java.util.List;

/**
 * 处理投票人撤消注册
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/
@Component("VOTER_CANNEL")
public class VoterCannelHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(VoterCannelHandler.class);

    @Autowired
    private DBAccess dbAccess;
    //撤销投票
    @Override
    protected void handle(Transaction transaction) {
        String payAddress = new String(transaction.getPayAddress());
        Optional<Voter> voterOptional = dbAccess.getVoter(payAddress);
        if (voterOptional.isPresent() && voterOptional.get().getStatus() == 1) {//只有投票人才能发起取消注册
            //判断投票人资产是否足够
            Optional<Account> accountOptional = dbAccess.getAccount(payAddress+"_"+Constant.MAIN_COIN);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                BigDecimal balance = account.getBalance();
                BigDecimal result = balance.add(Constant.FEE_4_REGISTER_VOTER);

                //覆盖原来记录
                Voter voter = voterOptional.get();
                voter.setStatus(0);
                //TODO 查询投票记录，归还用户投票
                boolean flag = dbAccess.putVoter(voter);
                if (flag) {
                    account.setBalance(result.subtract(getFee(transaction)));
                    if(account.getBalance().compareTo(BigDecimal.ZERO) < 0){
                        return;
                    }
                    dbAccess.putAccount(account);
                    //撤消相对应的投票
                    List<VoteRecord> voteRecords = dbAccess.listVoteRecords(payAddress,"payAddress");
                    if(voteRecords != null){
                        for (VoteRecord voteRecord : voteRecords){
                            //撤销投票
                            voteRecord.setStatus(0);
                            dbAccess.putVoteRecord(voteRecord);
                            int number = voteRecord.getVoteNum();
                            String receiptAddr = voteRecord.getReceiptAddress();
                            Optional<Trustee> trusteeOptional = dbAccess.getTrustee(receiptAddr);
                            if(trusteeOptional.isPresent()){
                                Trustee trustee = trusteeOptional.get();
                                trustee.setVotes(trustee.getVotes()-number);
                                dbAccess.putTrustee(trustee);
                            }
                        }
                    }
                }
            }
        }
    }
}
