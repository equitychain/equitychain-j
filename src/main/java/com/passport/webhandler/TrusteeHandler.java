package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.annotations.RocksTransaction;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.BlockUtils;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        if(objectOptional.isPresent()){
            List<Trustee> list = (List<Trustee>)objectOptional.get();
            for(Trustee tee : list){
                if(tee.getAddress().equals(trustee.getAddress())){
                    tee.setStatus(0);//状态设置为已出场
                    break;
                }
            }
            dbAccess.put(String.valueOf(blockCycle), list);
        }
    }

    /**
     * 找出还未出块的受托人
     * @param blockCycle
     * @return
     */
    public List<Trustee> findValidTrustees(int blockCycle) {
        List<Trustee> trustees = new ArrayList<>();
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        if(objectOptional.isPresent()){
            List<Trustee> list = (List<Trustee>)objectOptional.get();
            for(Trustee tee : list){
                if(tee.getStatus() == 1){
                    trustees.add(tee);
                }
            }
        }
        return trustees;
    }

    public List<Trustee> getTrusteesBeforeTime(long newBlockHeight, int blockCycle) {
        Long timestamp = blockUtils.getTimestamp4BlockCycle(newBlockHeight);
        //查询投票记录（status==1）,时间小于等于timestamp，按投票票数从高到低排列的101个受托人，放到101个受托人列表中
        List<Trustee> trustees = new ArrayList<>();
        List<Trustee> remoreTrus = new ArrayList<>();
        try {
            trustees = dbAccess.getTrusteeOfRangeBeforeTime(timestamp);
            for(Trustee trustee:trustees){//周期结束下个周期用户未启动情况
                if(CollectionUtils.isEmpty(dbAccess.seekByKey(trustee.getAddress()))){
                    remoreTrus.add(trustee);
                }
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        trustees.removeAll(remoreTrus);
        dbAccess.put(String.valueOf(blockCycle), trustees);

        return trustees;
    }
}
