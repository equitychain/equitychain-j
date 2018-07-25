package com.passport.miner;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        //构造最新区块
        Block prevBlock = lastBlock.get();
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setTimeStamp(System.currentTimeMillis());
        blockHeader.setHashPrevBlock(prevBlock.getBlockHeader().getHashMerkleRoot());
        blockHeader.setHashMerkleRoot("helloMerkleRoot".getBytes());//TODO 计算本节点的merkleRoot

        Block currentBlock = new Block();
        currentBlock.setBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.setBlockHeader(blockHeader);
        currentBlock.setBlockSize(100L);

        //创建挖矿奖励交易
        Transaction transaction = new Transaction();
        transaction.setPayAddress("123".getBytes());
        transaction.setReceiptAddress("456".getBytes());
        List<Transaction> list = new ArrayList<>();
        list.add(transaction);

        currentBlock.setTransactions(list);

        //把新增的区块存储到本地
        dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
        dbAccess.putBlock(currentBlock);
    }
}
