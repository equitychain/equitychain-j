package com.passport.timer;

import com.google.common.base.Optional;
import com.passport.annotations.RocksTransaction;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.BlockUtils;
import com.passport.webhandler.BlockHandler;
import com.passport.webhandler.TrusteeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    private DBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private TrusteeHandler trusteeHandler;
    @Autowired
    private BlockHandler blockHandler;

    @RocksTransaction
    public void monitor() throws InterruptedException {
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
        long currentTimeStamp = System.currentTimeMillis();
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

    public static volatile boolean nextBlockFlag = true;//默认处理发布主动同步区块事件，为false则处理组2接收广播区块、接收广播流水

    @RocksTransaction
    public void checkBlock() throws InterruptedException {
        if(nextBlockFlag){
            Timer timer = new Timer ( );
            timer.schedule ( new TimerTask( ) {
                @Override
                public void run() {
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
                    long currentTimeStamp = System.currentTimeMillis();
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
                    try {
                        blockHandler.produceNextBlock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 10000, 10000 );
        }
    }
}
