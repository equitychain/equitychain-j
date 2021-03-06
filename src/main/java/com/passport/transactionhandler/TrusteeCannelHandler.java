
package com.passport.transactionhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.CastUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;


/**
 * 处理委托人撤消注册
 * @author: xujianfeng
 * @create: 2018-09-10 17:46
 **/

@Component("TRUSTEE_CANNEL")
public class TrusteeCannelHandler extends TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeCannelHandler.class);

    @Autowired
    private DBAccess dbAccess;

    @Override
    protected void handle(Transaction transaction) {
        String payAddress = new String(transaction.getPayAddress());
        Optional<Trustee> trusteeOptional = dbAccess.getTrustee(payAddress);
        if (trusteeOptional.isPresent() && trusteeOptional.get().getStatus() == 1) {//只有受托人才能发起取消注册
            Optional<Account> accountOptional = dbAccess.getAccount(payAddress+"_"+Constant.MAIN_COIN);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                BigDecimal balance = account.getBalance();
                BigDecimal result = balance.add(Constant.FEE_4_REGISTER_TRUSTEE);

                //覆盖原来记录
                Trustee trustee = trusteeOptional.get();
                trustee.setStatus(0);
                //TODO 查询投票记录，归还用户投票
                boolean flag = dbAccess.putTrustee(trustee);
                if (flag) {
                    account.setBalance(result.subtract(getFee(transaction)));
                    if(account.getBalance().compareTo(BigDecimal.ZERO) < 0){
                        return;
                    }
                    dbAccess.putAccount(account);
                    //撤销注册为受托人成功后需要返还票数
                    List<VoteRecord> voteRecords = dbAccess.listVoteRecords(payAddress,"receiptAddress");
                    if(voteRecords != null){
                        for(VoteRecord voteRecord : voteRecords){
                            voteRecord.setStatus(0);
                            dbAccess.putVoteRecord(voteRecord);
                            int number = voteRecord.getVoteNum();
                            String payAddr = voteRecord.getPayAddress();
                            Optional<Voter> voterOptional = dbAccess.getVoter(payAddr);
                            if(voterOptional.isPresent()){
                                Voter voter = voterOptional.get();
                                voter.setVoteNum(voter.getVoteNum() + number);
                                dbAccess.putVoter(voter);
                            }
                        }
                    }
                }
            }
        }
    }
}

