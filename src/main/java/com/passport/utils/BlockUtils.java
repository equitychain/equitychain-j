package com.passport.utils;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 区块相关工具类
 * @author: xujianfeng
 * @create: 2018-09-14 11:33
 **/
@Component
public class BlockUtils {
    @Autowired
    private DBAccess dbAccess;

    /**
     * 101个受托人出块为一个周期，获取当前区块高度所在的周期
     * @param blockHeight
     * @return
     */
    public int getBlockCycle(long blockHeight){
        Double result = Math.floor(blockHeight / Constant.TRUSTEES_INIT_NUM) + (blockHeight % Constant.TRUSTEES_INIT_NUM > 0 ? 1 : 0);
        return result.intValue();
    }

    /**
     * 出块周期对应的开始时间
     * @param blockHeight
     * @return
     */
    public Long getTimestamp4BlockCycle(long blockHeight){
        int blockCycle = getBlockCycle(blockHeight);
        //第一个出块周期以创世块的投票记录为依据，每个出块周期的投票截止时间计算公式：（周期数-1）*101*10+创世时间
        long timeGapSecond = (blockCycle - 1) * Constant.TRUSTEES_INIT_NUM * Constant.BLOCK_GENERATE_TIMEGAP * 1000;
        Optional<Block> block = dbAccess.getBlock(1);
        if(block.isPresent()){
            return block.get().getBlockHeader().getTimeStamp() + timeGapSecond;
        }
        return null;
    }

    /**
     * 按规则选取出块者
     * @param trustees
     * @param blockHeight
     * @return
     */
    public Trustee randomPickBlockProducer(List<Trustee> trustees, long blockHeight){
        Long index = blockHeight * 100 % trustees.size();
        return trustees.get(index.intValue());
    }
}
