package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.db.dbhelper.DBAccess;
import com.passport.enums.TransactionTypeEnum;
import com.passport.event.SyncBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.GsonUtils;
import com.passport.utils.RawardUtil;
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

    /**
     * 流水打包
     */
    public void packagingBlock(Account minerAccount) {
        Optional<Block> lastBlock = dbAccess.getLastBlock();
        if (!lastBlock.isPresent()) {//系统初始化的时候就应该创建创世块，应用运行时不承担创建的职责
            return;
        }
        Block prevBlock = lastBlock.get();//上个区块

        //区块头，merkleTree和hash应该在获得打包交易权限时生成
        BlockHeader currentBlockHeader = new BlockHeader();
        currentBlockHeader.setTimeStamp(System.currentTimeMillis());
        currentBlockHeader.setHashPrevBlock(prevBlock.getBlockHeader().getHash());
        //todo 这里是设置区块最多能打包多少的流水egg消耗
        currentBlockHeader.setEggMax(Long.parseLong("1000"));

        //创建挖矿奖励交易
        Transaction transaction = new Transaction();
        transaction.setPayAddress(null);
        transaction.setReceiptAddress(minerAccount.getAddress().getBytes());//奖励接收者是挖矿账号
        transaction.setValue(String.valueOf(RawardUtil.getRewardByHeight(prevBlock.getBlockHeight() + 1).toString()).getBytes());//TODO 挖矿奖励取值优化
        transaction.setExtarData("挖矿奖励".getBytes());
        transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
        //生成hash和生成签名sign使用的基础数据都应该一样
        String transactionJson = GsonUtils.toJson(transaction);
        //计算交易hash
        transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());
        transaction.setTradeType(TransactionTypeEnum.BLOCK_REWARD.getDesc().getBytes());

        List<Transaction> list = new ArrayList<>();
        list.add(transaction);

        //构造当前区块
        Block currentBlock = new Block();
        currentBlock.setBlockHeader(currentBlockHeader);
        currentBlock.setBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.setTransactions(list);
        currentBlock.setTransactionCount(list.size());

        //完成共识，打包交易流水
        List<Transaction> transactions = dbAccess.listUnconfirmTransactions();
        List<Transaction> blockTrans = transactionHandler.getBlockTrans(transactions,new BigDecimal(currentBlockHeader.getEggMax()));
        blockTrans.forEach((tran)->{
            //矿工费付给矿工  注意!无论流水是否成功被打包该矿工费是必须给的,因为已经扣了,
            Transaction feeTrans = new Transaction();
            feeTrans.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
            feeTrans.setExtarData("流水矿工费获得".getBytes());
            BigDecimal valueDec = transactionHandler.getTempEggByHash(tran.getHash());
            valueDec = valueDec == null?BigDecimal.ZERO:valueDec;
            feeTrans.setValue(String.valueOf(valueDec).getBytes());
            feeTrans.setReceiptAddress(minerAccount.getAddress().getBytes());
            //添加奖励和需要确认的流水
            currentBlock.getTransactions().add(feeTrans);
            currentBlock.getTransactions().add(tran);
        });

        //执行流水
        transactionHandler.exec(currentBlock.getTransactions());

        //把新增的区块存储到本地
        dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.calculateFieldValueWithHash();
        dbAccess.putBlock(currentBlock);
        provider.publishEvent(new SyncBlockEvent(currentBlock));
    }

    @Deprecated
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
        //todo 这里是设置区块最多能打包多少的流水egg消耗
        currentBlockHeader.setEggMax(Long.parseLong("1000"));
        //获取挖矿账户
        Optional<Account> minerAccountOptional = dbAccess.getMinerAccount();
        if(!minerAccountOptional.isPresent()){
            return;//TODO 应提示挖矿账户不存在
        }
        Account minerAccount = minerAccountOptional.get();

        //创建挖矿奖励交易
        Transaction transaction = new Transaction();
        transaction.setReceiptAddress(minerAccount.getAddress().getBytes());//奖励接收者是挖矿账号
        transaction.setValue(String.valueOf(RawardUtil.getRewardByHeight(prevBlock.getBlockHeight() + 1).toString()).getBytes());//TODO 挖矿奖励取值优化
        transaction.setExtarData("挖矿奖励".getBytes());
        transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
        //生成hash和生成签名sign使用的基础数据都应该一样
        String transactionJson = GsonUtils.toJson(transaction);
        //计算交易hash
        transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());

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
        List<Transaction> blockTrans = transactionHandler.getBlockTrans(transactions,new BigDecimal(currentBlockHeader.getEggMax()));
        blockTrans.forEach((tran)->{
            //矿工费付给矿工  注意!无论流水是否成功被打包该矿工费是必须给的,因为已经扣了,
            Transaction feeTrans = new Transaction();
            feeTrans.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
            feeTrans.setExtarData("流水矿工费获得".getBytes());
            BigDecimal valueDec = transactionHandler.getTempEggByHash(tran.getHash());
            valueDec = valueDec == null?BigDecimal.ZERO:valueDec;
            feeTrans.setValue(String.valueOf(valueDec).getBytes());
            if(minerAccountOptional.isPresent()){
                feeTrans.setReceiptAddress(minerAccountOptional.get().getAddress().getBytes());
            }
            //添加奖励和需要确认的流水
            currentBlock.getTransactions().add(feeTrans);
            currentBlock.getTransactions().add(tran);
        });

        //执行流水
        transactionHandler.exec(currentBlock.getTransactions());

        //把新增的区块存储到本地
        dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.calculateFieldValueWithHash();
        dbAccess.putBlock(currentBlock);
        provider.publishEvent(new SyncBlockEvent(currentBlock));
    }
}
