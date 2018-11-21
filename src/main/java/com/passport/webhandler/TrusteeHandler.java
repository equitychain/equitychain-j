package com.passport.webhandler;

import com.passport.constant.SyncFlag;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.utils.BlockUtils;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrusteeHandler {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeHandler.class);

    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;

    /**
     * 改变已经出块人的状态
     * @param trustee
     * @param blockCycle
     */
    public void changeStatus(Trustee trustee, int blockCycle) {
        List<Trustee> list = SyncFlag.blockCycleList.get("blockCycle");
        for(Trustee tee : list){
            if(tee.getAddress().equals(trustee.getAddress())){
                tee.setStatus(0);//状态设置为已出场
                break;
            }
        }
        dbAccess.put(String.valueOf(blockCycle), list);
        SyncFlag.blockCycleList.put("blockCycle", list);
        logger.info("改变当前出块人的状态"+trustee.getAddress());
    }

    /**
     * 找出还未出块的受托人
     * @param blockCycle
     * @return
     */
    public List<Trustee> findValidTrustees(int blockCycle) {
        List<Trustee> trustees = new ArrayList<>();
        List<Trustee> list = SyncFlag.blockCycleList.get("blockCycle");
        if(!CollectionUtils.isEmpty(list)){
            for(Trustee tee : list){
                if(tee.getStatus() == 1 && tee.getState() != 0){
                    trustees.add(tee);
                }
            }
        }
        logger.info("受托人列表数量："+trustees.size()+"------"+trustees);
        return trustees;
    }

    public List<Trustee> getTrusteesBeforeTime(long newBlockHeight, int blockCycle) {
        logger.info("重新获取受托人列表");
        Long timestamp = blockUtils.getTimestamp4BlockCycle(newBlockHeight);
        //查询投票记录（status==1）,时间小于等于timestamp，按投票票数从高到低排列的101个受托人，放到101个受托人列表中
        List<Trustee> trustees = new ArrayList<>();
        try {
            List<Trustee> tru = dbAccess.listTrustees();
            logger.info("缓存中存在已启动节点："+SyncFlag.waitMiner.size());
            SyncFlag.waitMiner.forEach((k, v) ->{//更新缓存中准备启动出块节点账号
                for(Trustee trustee:tru){
                    if(trustee.getAddress().equals(k)){
                        trustee.setState(v);
                        SyncFlag.waitMiner.remove(k);
                        logger.info("新周期移除缓存中节点："+SyncFlag.waitMiner.size());
                        dbAccess.putTrustee(trustee);
                    }
                }
            });
            trustees = dbAccess.getTrusteeOfRangeBeforeTime(timestamp);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        dbAccess.put(String.valueOf(blockCycle), trustees);
        SyncFlag.blockCycleList.put("blockCycle", trustees);
        logger.info(trustees.size()+"新周期列表为："+trustees);
        return trustees;
    }
}
