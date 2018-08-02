package com.passport.miner;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.eth.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: xujianfeng
 * @create: 2018-07-23 15:21
 **/
@Component
public class DPOSMiner {
    @Autowired
    private DBAccess dbAccess;

    public void mining() {
        //TODO 这里还需要设置获取收益的挖矿账户

        Optional<Block> lastBlock = dbAccess.getLastBlock();
        if (!lastBlock.isPresent()) {//系统初始化的时候就应该创建创世块，应用运行时不承担创建的职责
            return;
        }

        //根据上个区块构造最新区块
        Block prevBlock = lastBlock.get();

        //区块头，merkleTree和hash应该在获得打包交易权限时生成
        BlockHeader currentBlockHeader = new BlockHeader();
        currentBlockHeader.setTimeStamp(System.currentTimeMillis());
        currentBlockHeader.setHashPrevBlock(prevBlock.getBlockHeader().getHash());

        //创建挖矿奖励交易，来源地址
        Transaction transaction = new Transaction();
        transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
        transaction.setExtarData("挖矿奖励".getBytes());
        transaction.setValue(String.valueOf(new BigDecimal("10")).getBytes());//TODO 挖矿奖励取值优化

        transaction.setReceiptAddress(null);//奖励接收者是挖矿账号

        List<Transaction> list = new ArrayList<>();
        list.add(transaction);

        Block currentBlock = new Block();
        currentBlock.setBlockHeader(currentBlockHeader);
        currentBlock.setBlockHeight(prevBlock.getBlockHeight() + 1);

        currentBlock.setTransactions(list);

        //把新增的区块存储到本地
        dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
        dbAccess.putBlock(currentBlock);
    }
}
