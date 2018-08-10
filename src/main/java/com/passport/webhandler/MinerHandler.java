package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.eth.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 处理
 * @author: xujianfeng
 * @create: 2018-07-26 17:17
 **/
@Component
public class MinerHandler {
    private static final Logger logger = LoggerFactory.getLogger(MinerHandler.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private TransactionHandler transactionHandler;
    @Autowired
    private ApplicationContextProvider provider;

    public void mining() {
        Optional<Block> lastBlock = dbAccess.getLastBlock();
        if (!lastBlock.isPresent()) {//系统初始化的时候就应该创建创世块，应用运行时不承担创建的职责
            return;
        }
        Block prevBlock = lastBlock.get();//上个区块

        //区块头，merkleTree和hash应该在获得打包交易权限时生成
        BlockHeader currentBlockHeader = new BlockHeader();
        currentBlockHeader.setTimeStamp(System.currentTimeMillis());
        currentBlockHeader.setHashPrevBlock(prevBlock.getBlockHeader().getHash());

        //创建挖矿奖励交易
        Transaction transaction = new Transaction();
        transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
        transaction.setExtarData("挖矿奖励".getBytes());
        transaction.setValue(String.valueOf(new BigDecimal("10")).getBytes());//TODO 挖矿奖励取值优化
        Optional<Account> minerAccount = dbAccess.getMinerAccount();
        if(minerAccount.isPresent()){
            transaction.setReceiptAddress(minerAccount.get().getAddress().getBytes());//奖励接收者是挖矿账号
        }
        List<Transaction> list = new ArrayList<>();
        list.add(transaction);

        //构造当前区块
        Block currentBlock = new Block();
        currentBlock.setBlockHeader(currentBlockHeader);
        currentBlock.setBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.setTransactions(list);
        currentBlock.setTransactionCount(list.size());

        //使用线程休眠模拟共识 TODO 待实现
        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //完成共识，打包交易流水
        List<Transaction> transactions = dbAccess.listUnconfirmTransactions();
        transactions.forEach((Transaction trans) -> {
            currentBlock.getTransactions().add(trans);
        });

        //执行流水
        transactionHandler.exec(currentBlock);

        //把新增的区块存储到本地
        dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
        dbAccess.putBlock(currentBlock);

        provider.publishEvent(new SyncBlockEvent(currentBlock));
    }
}
