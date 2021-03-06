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
import com.passport.utils.*;
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

import java.util.*;

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
    private StoryFileUtil storyFileUtil;

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
        Map<String,Object> map = new HashMap<>();
        String payAddress = "bx85fa7670e2f66024a39a06d2636142d4a7ed6fa3";
        Set<String> address = storyFileUtil.getAddresses();
        List<String> list = new ArrayList<>(address);
        String receiptAddress = list.get(RandomUtils.nextInt(0,list.size()));
        String value = RandomUtils.nextInt(1,11)+"";
        String extarData = "测试";
        String password ="123456";
        String tradeType = "TRANSFER";
        String token = "EQU";
        map.put("payAddress",payAddress);
        map.put("receiptAddress",receiptAddress);
        map.put("value",value);
        map.put("extarData",extarData);
        map.put("password",password);
        map.put("tradeType",tradeType);
        map.put("token",token);
        String result = HttpUtils.doPost("http://47.75.4.251:8083/transaction/send",map);
        logger.info("测试定时任务发送流水成功"+result);
    }
}
