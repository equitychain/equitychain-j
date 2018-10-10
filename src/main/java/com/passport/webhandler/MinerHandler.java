package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.crypto.ECDSAUtil;
import com.passport.db.dbhelper.DBAccess;
import com.passport.enums.TransactionTypeEnum;
import com.passport.event.SyncBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
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
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private BlockHandler blockHandler;

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


        transaction.setValue(String.valueOf(blockHandler.getReward(CastUtils.castLong(prevBlock.getBlockHeight() + 1))).getBytes());//TODO 挖矿奖励取值优化
        transaction.setExtarData(TransactionTypeEnum.BLOCK_REWARD.toString().getBytes());
        transaction.setTime(String.valueOf(System.currentTimeMillis()).getBytes());
        transaction.setBlockHeight(((prevBlock.getBlockHeight() + 1)+"").getBytes());
        //生成hash和生成签名sign使用的基础数据都应该一样
        String transactionJson = GsonUtils.toJson(transaction);
        //计算交易hash
        transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());
        transaction.setTradeType(TransactionTypeEnum.BLOCK_REWARD.toString().getBytes());

        List<Transaction> list = new ArrayList<>();
        list.add(transaction);

        //构造当前区块
        Block currentBlock = new Block();
        currentBlock.setBlockHeader(currentBlockHeader);
        currentBlock.setBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.setTransactions(list);
        currentBlock.setTransactionCount(list.size());
        currentBlock.setProducer(minerAccount.getAddress());

        //完成共识，打包交易流水
        List<Transaction> transactions = dbAccess.listUnconfirmTransactions();
        List<Transaction> blockTrans = transactionHandler.getBlockTrans(transactions,new BigDecimal(currentBlockHeader.getEggMax()));
        BigDecimal sumTransMoney = BigDecimal.ZERO;
        for(Transaction tran : blockTrans){
            //矿工费
            BigDecimal valueDec = transactionHandler.getTempEggByHash(tran.getHash());
            valueDec = valueDec == null?BigDecimal.ZERO:valueDec;
            Optional<Account> account = dbAccess.getAccount(new String(tran.getPayAddress()));
            if(!account.isPresent()){
                continue;
            }
            Account acc = account.get();
            //判断金额是否足够扣除矿工费
            if(valueDec.add(new BigDecimal(new String(tran.getValue()))).compareTo(acc.getBalance()) <=  0) {
                //矿工费付给矿工  注意!无论流水是否成功被打包该矿工费是必须给的,因为已经扣了,
                Transaction feeTrans = new Transaction();
                feeTrans.setTime((System.currentTimeMillis() + "").getBytes());
                feeTrans.setPayAddress(null);
                feeTrans.setExtarData(tran.getHash());
                //受托人获取确认流水矿工费的一定比例的奖励
                feeTrans.setValue(String.valueOf(valueDec.multiply(BigDecimal.ONE.subtract(Constant.CONFIRM_TRANS_PROPORTION))).getBytes());
                feeTrans.setBlockHeight(((prevBlock.getBlockHeight() + 1) + "").getBytes());
                feeTrans.setReceiptAddress(minerAccount.getAddress().getBytes());

                //生成hash和生成签名sign使用的基础数据都应该一样
                String tranJson = GsonUtils.toJson(feeTrans);
                //计算交易hash
                feeTrans.setHash(ECDSAUtil.applySha256(tranJson).getBytes());
                feeTrans.setTradeType(TransactionTypeEnum.CONFIRM_REWARD.toString().getBytes());

                tran.setBlockHeight(((prevBlock.getBlockHeight() + 1) + "").getBytes());
                tran.setTime(feeTrans.getTime());
                //添加奖励和需要确认的流水
                currentBlock.getTransactions().add(feeTrans);
                currentBlock.getTransactions().add(tran);
                //计算分发的流水奖励金额的比例
                sumTransMoney = sumTransMoney.add(valueDec.multiply(Constant.CONFIRM_TRANS_PROPORTION));
            }
        }
        long time = blockUtils.getTimestamp4BlockCycle(prevBlock.getBlockHeight() + 1);
        //获取受托人的投票记录  某个时间前的
        List<VoteRecord> voteRecords = dbAccess.listVoteRecords(minerAccount.getAddress(),
                "receiptAddress",time,2);
        //计算每个投票人应该获得多少奖励
        BigDecimal voterReward = sumTransMoney.divide(new BigDecimal(voteRecords.size()),Constant.PROPORTION_ACCURACY,BigDecimal.ROUND_DOWN);
        //差值计算
        BigDecimal diffReward = sumTransMoney.subtract(voterReward.multiply(new BigDecimal(voteRecords.size())));
        for(int i = 0; i < voteRecords.size(); i ++){
            VoteRecord record = voteRecords.get(i);
            Transaction feeTrans = new Transaction();
            feeTrans.setTime((System.currentTimeMillis()+"").getBytes());
            feeTrans.setPayAddress(null);
            feeTrans.setExtarData(Constant.VOTER_TRANS_PROPORTION_EXTAR_DATA.getBytes());
            feeTrans.setBlockHeight(((prevBlock.getBlockHeight() + 1)+"").getBytes());
            if(i != voteRecords.size()-1) {
                feeTrans.setValue(String.valueOf(voterReward).getBytes());
            }else{
                feeTrans.setValue(String.valueOf(voterReward.add(diffReward)).getBytes());
            }
            feeTrans.setReceiptAddress(record.getPayAddress().getBytes());
            String tranJson = GsonUtils.toJson(feeTrans);
            feeTrans.setHash(ECDSAUtil.applySha256(tranJson).getBytes());
            feeTrans.setTradeType(TransactionTypeEnum.CONFIRM_REWARD.toString().getBytes());
            currentBlock.getTransactions().add(feeTrans);
        }
        //执行流水
        transactionHandler.exec(currentBlock.getTransactions());

        //把新增的区块存储到本地
        dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
        currentBlock.calculateFieldValueWithHash();
        dbAccess.putBlock(currentBlock);
        provider.publishEvent(new SyncBlockEvent(currentBlock));
    }
}
