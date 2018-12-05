package com.passport.timer;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.utils.BlockUtils;
import com.passport.utils.CheckUtils;
import com.passport.utils.DateUtils;
import com.passport.web.TransactionController;
import com.passport.webhandler.BlockHandler;
import com.passport.webhandler.TransactionHandler;
import com.passport.webhandler.TrusteeHandler;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 监控出块人有没按时出块
 *
 * @author: xujianfeng
 * @create: 2018-09-28 11:07
 **/
@Component
public class MonitoringIfProducerDead {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringIfProducerDead.class);

    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private TrusteeHandler trusteeHandler;
    @Autowired
    private BlockHandler blockHandler;
    @Autowired
    private TransactionHandler transactionHandler;

//    private Lock lock = new ReentrantLock();
    public void monitor() throws Exception {
        //已同步完成，切换到接收区块和流水广播状态
        if (SyncFlag.isNextBlockSyncFlag()) {
            return;
        }

        //最后一个区块出块时间距离现在超过10秒
        Optional<Block> lastBlockOptional = dbAccess.getLastBlock();
        if (!lastBlockOptional.isPresent()) {
            return;
        }
        Block block = lastBlockOptional.get();
        Long timeStamp = block.getBlockHeader().getTimeStamp();
//        long currentTimeStamp = NetworkTime.INSTANCE.getWebsiteDateTimeLong();
        long currentTimeStamp = DateUtils.getWebTime();
        if (currentTimeStamp <= timeStamp + Constant.BLOCK_GENERATE_TIMEGAP * 1000) {
            return;
        }

        //算出原本应该出块的账户，把这个账户从委托人中剔除
        Long newBlockHeight = block.getBlockHeight();
        int blockCycle = blockUtils.getBlockCycle(newBlockHeight);
        List<Trustee> trustees = trusteeHandler.findValidTrustees(blockCycle);
        if(trustees.size() == 0){
            trustees = trusteeHandler.getTrusteesBeforeTime(newBlockHeight, blockCycle);
        }
        Trustee trustee = blockUtils.randomPickBlockProducer(trustees, newBlockHeight+1);
        trusteeHandler.changeStatus(trustee, blockCycle);

        //再次选出出块账户
        blockHandler.produceNextBlock();
    }
//    @Scheduled(cron = "0/1 * * * * ?")
    public void test(){
        if(!SyncFlag.isNextBlockSyncFlag()){
            String payAddress = "bxbc6daf9e6d3c2cc1ed995e815f8daeb53dee2a7b";
            String receiptAddress = "bxdbaac3b141b6d20ce8a056e57e6f29388fd8399b";
            String value = RandomUtils.nextInt(1,11)+"";
            String extarData = "测试";
            String password ="123456";
            String tradeType = "TRANSFER";
            String token = "EQU";
            boolean flag = false;
            //若流水类型为 委托人注册 或 投票人注册的时候 不校验receiptAddress
            if (TransactionTypeEnum.TRUSTEE_REGISTER.toString().equals(tradeType)
                    || TransactionTypeEnum.VOTER_REGISTER.toString().equals(tradeType)) {
                flag = CheckUtils.checkParamIfEmpty(payAddress, value, extarData);
            } else {
                //非空检验
                flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
            }
            Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password, tradeType,token);
            logger.info("测试定时任务发送流水成功"+transaction);
        }else{
            logger.info("需同步完成才能启动定时任务");
        }
    }
}
