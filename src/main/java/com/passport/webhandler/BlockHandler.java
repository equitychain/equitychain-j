package com.passport.webhandler;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import com.google.protobuf.ByteString;
import com.passport.annotations.RocksTransaction;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.*;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.DBAccess;
import com.passport.db.dbhelper.IndexColumnNames;
import com.passport.enums.TransactionTypeEnum;
import com.passport.event.GenerateNextBlockEvent;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.BlockHeaderMessage;
import com.passport.proto.BlockMessage;
import com.passport.proto.TransactionMessage;
import com.passport.timer.MonitoringIfProducerDead;
import com.passport.transactionhandler.TransactionStrategy;
import com.passport.transactionhandler.TransactionStrategyContext;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.RawardUtil;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableAsync
public class BlockHandler {
    private static final Logger logger = LoggerFactory.getLogger(BlockHandler.class);

    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    @Autowired
    private MinerHandler minerHandler;
    @Autowired
    private TrusteeHandler trusteeHandler;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private TransactionStrategyContext transactionStrategyContext;

    public volatile boolean padding = false;

    /**
     * 根据区块高度获取出块奖励，每年递减，第6年及以后奖励恒定
     * @param blockHeight
     * @return
     */
    public BigDecimal getReward(Long blockHeight){
        Long index = blockHeight/Constant.BLOCK_DISTANCE;
        if(index > Constant.REWARD_ARRAY.length-1){
            return Constant.REWARD_ARRAY[Constant.REWARD_ARRAY.length-1];
        }else{
            return Constant.REWARD_ARRAY[index.intValue()];
        }
    }

    /**
     * 校验区块是否合法
     * @param block
     * @return
     */
    public boolean checkBlock(Block block) {
        Long blockHeight = block.getBlockHeight();

        //创世区块，不需要校验和前一个区块的hash
        if (blockHeight == 1) {//比对计算出来的hash和传过来的hash是否一致
            return Objects.equal(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
        }

        //后续区块
        if (blockHeight > 1) {
            boolean flag = Arrays.equals(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
            if(!flag){
                return false;
            }

            Optional<Block> prevBlock = dbAccess.getBlock(blockHeight - 1);
            if(prevBlock.isPresent()){
                byte[] hashOfPrevBlock = prevBlock.get().getBlockHeader().getHash();//前一个区块hash
                byte[] prevHashOfCurrentBlock = block.getBlockHeader().getHashPrevBlock();//当前区块的前一个区块hash
                if (!Arrays.equals(hashOfPrevBlock,prevHashOfCurrentBlock)) {//前一个区块的hash和当前区块的前一个区块hash是否相等
                    return false;
                }
            }
            //奖励金额的判断
            List<Transaction> transactions = block.getTransactions();
            if(transactions == null || transactions.size() == 0){
                return false;
            }
            //奖励金额的合法性判断
            boolean reword = false;
            String receiptAddress = null;
            for(Transaction tran : transactions) {
                byte[] payAddr = tran.getPayAddress();
                byte[] compByt = TransactionTypeEnum.BLOCK_REWARD.toString().getBytes();
                if ((payAddr == null||payAddr.length==0) && tran.getExtarData() != null && Arrays.equals(compByt, tran.getExtarData())) {
                    //奖励的流水
                    byte[] value = tran.getValue();
                    receiptAddress = new String(tran.getReceiptAddress());
                    reword = RawardUtil.checkReward(blockHeight, value);
                    break;
                }
            }
            /*if(reword){
                //校验是否轮到该用户出块
                int blockCycle = blockUtils.getBlockCycle(blockHeight);
                List<Trustee> trustees = trusteeHandler.findValidTrustees(blockCycle);
                Trustee trustee = blockUtils.randomPickBlockProducer(trustees, blockHeight);
                if(trustee == null || !trustee.getAddress().equals(receiptAddress)){
                    return false;
                }
            }*/
            return reword;
        }

        return false;
    }
    public synchronized void addBlockQueue(List<Block> blocks) throws InterruptedException {
        if(!padding) {
            //排序，根据高度排序 排序后再添加
            blocks.sort((block1,block2)->{
                return block1.getBlockHeight().compareTo(block2.getBlockHeight());
            });
            //添加到队列中
            Constant.BLOCK_QUEUE.offer(blocks);
            int size = Constant.BLOCK_QUEUE.size();
            if(size == Constant.BLOCK_NODE_COUNT){
                //满了，进行校验
                padding = true;
                //异步处理,不然其他的都在处于等待
                synHandlerBlock();
            }
        }else{
            //todo 满了，正在处理
        }
    }

    public void synHandlerBlock(){
        //TODO 需不需要额外开线程，需要的话可以写个线程工具类
        Thread handlerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dbAccess.transaction =dbAccess.rocksDB.beginTransaction(new WriteOptions());;

                try{
                    //todo 校验 目前是获取相同的区块高度
                    List<Block> successBlocks = getShareBlocks();
                    //存储区块到本地
                    for(Block blockLocal : successBlocks) {
                        Optional<Object> optHeigth = dbAccess.getLastBlockHeightT();
                        if(optHeigth.isPresent()) {
                            Long height = (Long)optHeigth.get();
                            if(height != null) {
                                if((blockLocal.getBlockHeight() - height) == 1) {
                                    dbAccess.putBlock(blockLocal);
                                    dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());

                                    //同时保存区块中的流水到已确认流水列表中
                                    blockLocal.getTransactions().forEach(transaction -> {
                                        TransactionStrategy transactionStrategy = transactionStrategyContext.getTransactionStrategy(new String(transaction.getTradeType()));
                                        if(transactionStrategy != null){
                                            transactionStrategy.handleTransaction(transaction);
                                            try {
                                                dbAccess.addIndex(transaction, IndexColumnNames.TRANSTIMEINDEX,transaction.getTime());
                                                dbAccess.addIndex(transaction,IndexColumnNames.TRANSBLOCKHEIGHTINDEX,transaction.getBlockHeight());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            dbAccess.putConfirmTransaction(transaction);
                                        }
                                    });
                                }
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                    dbAccess.transaction.commit();
                }catch (Exception e){
                    try {
                        dbAccess.transaction.rollback();
                    } catch (RocksDBException e1) {
                        e1.printStackTrace();
                    }
                    logger.warn("synchronization block error", e);
                }finally {
                    //更改状态
                    padding = false;
                    //清空队列
                    Constant.BLOCK_QUEUE.clear();

                    //继续同步下组区块
                    provider.publishEvent(new SyncNextBlockEvent(0L));
                }
            }
        });
        handlerThread.start();
    }
    //检查各节点区块，取出共用的区块高度 并是连续的
    protected List<Block> getShareBlocks(){
        Iterator<List<Block>> iterator = Constant.BLOCK_QUEUE.iterator();
        List<Block> baseBlocks = null;
        //获取第一个节点的数据
        if(iterator.hasNext()){
            List<Block> blocks = iterator.next();
            baseBlocks = new ArrayList<>();
            //连续
            for (int i = 0; i < blocks.size(); i ++){
                if(i == 0){
                    baseBlocks.add(blocks.get(i));
                }else{
                    Block b1 = blocks.get(i);
                    Block b2 = blocks.get(i-1);
                    if(b1.getBlockHeight()-b2.getBlockHeight() == 1){
                        baseBlocks.add(blocks.get(i));
                    }else {
                        break;
                    }
                }
            }
        }else{
            return new ArrayList<>();
        }
        if(iterator.hasNext()) {
            //某个节点的区块与其余节点的区块进行比较
            while (iterator.hasNext()) {
                List<Block> blocks = iterator.next();
                //判断哪个高度不一致,不一致的以下全部丢弃,只保留公共的/连续的
                int subIndex = 0;
                for (int i = 0; i < baseBlocks.size(); i ++) {
                    Block hasBlock = baseBlocks.get(i);
                    if(blocks.contains(hasBlock)){
                        subIndex = i;
                        break;
                    }
                }
                if(subIndex != 0){
                    for(int j = subIndex ; j < baseBlocks.size(); j ++) {
                        baseBlocks.remove(j);
                    }
                }
            }
        }
        return baseBlocks;
    }
    /**
     * protobuf block转成本地block
     * @param blockMessage
     * @return
     */
    public Block convertBlockMessage2Block(BlockMessage.Block blockMessage){
        //构造区块头
        BlockHeader blockHeader = new BlockHeader();
        BlockHeaderMessage.BlockHeader blockHeaderMessage = blockMessage.getBlockHeader();
        blockHeader.setTimeStamp(blockHeaderMessage.getTimeStamp());
        blockHeader.setHashPrevBlock(blockHeaderMessage.getHashPrevBlock().toByteArray());
        blockHeader.setHashMerkleRoot(blockHeaderMessage.getHashMerkleRoot().toByteArray());
        blockHeader.setHash(blockHeaderMessage.getHash().toByteArray());

        //构造区块
        Block block = new Block();
        block.setBlockSize(blockMessage.getBlockSize());
        block.setBlockHeader(blockHeader);
        block.setTransactionCount(blockMessage.getTransactionsCount());
        block.setBlockHeight(blockMessage.getBlockHeight());
        block.setTransactions(new ArrayList<>());
        block.setProducer(blockMessage.getProducer());
        //区块流水记录
        blockMessage.getTransactionsList().forEach((TransactionMessage.Transaction trans) -> {
            Transaction transaction = new Transaction();
            transaction.setHash(trans.getHash().toByteArray());
            transaction.setSignature(trans.getSignature().toByteArray());
            transaction.setValue(trans.getValue().toByteArray());
            transaction.setExtarData(trans.getExtarData().toByteArray());
            transaction.setPayAddress(trans.getPayAddress().toByteArray());
            transaction.setReceiptAddress(trans.getReceiptAddress().toByteArray());
            transaction.setEggPrice(trans.getEggPrice().toByteArray());
            transaction.setEggMax(trans.getEggMax().toByteArray());
            transaction.setTime(trans.getTimeStamp().toByteArray());
            transaction.setTradeType(trans.getTradeType().toByteArray());
            transaction.setBlockHeight(trans.getBlockHeight().toByteArray());
            transaction.setEggUsed(trans.getEggUsed().toByteArray());
            if(trans.getNonce().toByteArray() != null && trans.getNonce().toByteArray().length != 0 )transaction.setNonce(Integer.parseInt(new String(trans.getNonce().toByteArray())));
            transaction.setPublicKey(trans.getPublicKey().toByteArray());
            if(trans.getStatus().toByteArray() != null && trans.getStatus().toByteArray().length != 0) transaction.setStatus(Integer.parseInt(new String(trans.getStatus().toByteArray())));
            block.getTransactions().add(transaction);
        });

        return block;
    }


    /**
     * 本地block转成protobuf block
     * @param block
     * @return
     */
    public BlockMessage.Block.Builder convertBlock2BlockMessage(Block block){
        //构造区块头
        BlockHeaderMessage.BlockHeader.Builder blockHeaderBuilder = BlockHeaderMessage.BlockHeader.newBuilder();
        blockHeaderBuilder.setTimeStamp(block.getBlockHeader().getTimeStamp());
        blockHeaderBuilder.setHashPrevBlock(ByteString.copyFrom(block.getBlockHeader().getHashPrevBlock()));
        blockHeaderBuilder.setHashMerkleRoot(ByteString.copyFrom(block.getBlockHeader().getHashMerkleRoot()));
        blockHeaderBuilder.setHash(ByteString.copyFrom(block.getBlockHeader().getHash()));

        //构造区块
        BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
        blockBuilder.setBlockSize(block.getBlockSize());
        blockBuilder.setBlockHeader(blockHeaderBuilder.build());
        blockBuilder.setTransactionCount(block.getTransactionCount());
        blockBuilder.setBlockHeight(block.getBlockHeight());
        blockBuilder.setProducer(block.getProducer());

        //设置包含在区块中的流水记录
        block.getTransactions().forEach((Transaction trans) -> {
            TransactionMessage.Transaction.Builder transactionBuilder = TransactionMessage.Transaction.newBuilder();
            if(trans.getPayAddress()!=null)transactionBuilder.setPayAddress(ByteString.copyFrom(trans.getPayAddress()));
            if(trans.getReceiptAddress() != null) transactionBuilder.setReceiptAddress(ByteString.copyFrom(trans.getReceiptAddress()));
            if(trans.getValue() != null) transactionBuilder.setValue(ByteString.copyFrom(trans.getValue()));
            if(trans.getExtarData() != null) transactionBuilder.setExtarData(ByteString.copyFrom(trans.getExtarData()));
            if(trans.getTime() != null) transactionBuilder.setTimeStamp(ByteString.copyFrom(trans.getTime()));
            if(trans.getHash() != null) transactionBuilder.setHash(ByteString.copyFrom(trans.getHash()));
            if(trans.getSignature()!=null)transactionBuilder.setSignature(ByteString.copyFrom(trans.getSignature()));
            if(trans.getEggPrice()!=null)transactionBuilder.setEggPrice(ByteString.copyFrom(trans.getEggPrice()));
            if(trans.getEggMax()!=null)transactionBuilder.setEggMax(ByteString.copyFrom(trans.getEggMax()));
            if(trans.getTradeType()!=null)transactionBuilder.setTradeType(ByteString.copyFrom(trans.getTradeType()));
            if(trans.getBlockHeight()!=null)transactionBuilder.setBlockHeight(ByteString.copyFrom(trans.getBlockHeight()));
            if(trans.getEggUsed() != null) transactionBuilder.setEggUsed(ByteString.copyFrom(trans.getEggUsed()));
            if (trans.getNonce()!=null) transactionBuilder.setNonce(ByteString.copyFrom(trans.getNonce().toString().getBytes()));
            if(trans.getPublicKey()!=null)transactionBuilder.setPublicKey(ByteString.copyFrom(trans.getPublicKey()));
            if(trans.getStatus()!=null)transactionBuilder.setStatus(ByteString.copyFrom(trans.getStatus().toString().getBytes()));
            blockBuilder.addTransactions(transactionBuilder.build());
        });

        return blockBuilder;
    }

    public void produceNextBlock() throws InterruptedException {
        //当前区块周期
        Optional<Block> lastBlockOptional = dbAccess.getLastBlock();
        if(!lastBlockOptional.isPresent()){
            return;
        }
        Block block = lastBlockOptional.get();

        long blockHeight = CastUtils.castLong(block.getBlockHeight());
        long newBlockHeight = blockHeight + 1;
        int blockCycle = blockUtils.getBlockCycle(newBlockHeight);

        //出块完成后，计算出的下一个出块人如果是自己则继续发布出块事件
        List<Trustee> trustees = trusteeHandler.findValidTrustees(blockCycle);
        if(trustees.size() == 0){
            trustees = trusteeHandler.getTrusteesBeforeTime(newBlockHeight, blockCycle);
        }

        waitIfNotArrived(block);

        produceBlock(newBlockHeight, trustees, blockCycle);
    }

    /**
     * 未到出块时间则睡眠等待
     * @param block
     */
    private void waitIfNotArrived(Block block) {
        long lastTimestamp = block.getBlockHeader().getTimeStamp();
        long currentTimestamp = System.currentTimeMillis();
        long timeGap = currentTimestamp - lastTimestamp;
        if(timeGap < Constant.BLOCK_GENERATE_TIMEGAP*1000){//间隔小于10秒，则睡眠等待
            try {
                TimeUnit.MILLISECONDS.sleep(Constant.BLOCK_GENERATE_TIMEGAP*1000 - timeGap);
            } catch (InterruptedException e) {
                logger.error("生产区块睡眠等待异常", e);
            }
        }
    }

    /**
     * 打包区块、寻找下一个出块人是否在本节点
     * @param newBlockHeight 准备出块高度
     * @param list
     * @param blockCycle
     */
    public void produceBlock(long newBlockHeight, List<Trustee> list, int blockCycle) throws InterruptedException {
//        try{
            Trustee trustee = blockUtils.randomPickBlockProducer(list, newBlockHeight);
            Optional<Account> accountOptional = dbAccess.getAccount(trustee.getAddress());
            if(accountOptional.isPresent() && accountOptional.get().getPrivateKey() != null && !"".equals(accountOptional.get().getPrivateKey())){//出块人属于本节点
                MonitoringIfProducerDead.nextBlockFlag = false;
//                SyncFlag.setNextBlockSyncFlag(false);
                Account account = accountOptional.get();
                if(account.getPrivateKey() != null){
                    //打包区块
                    minerHandler.packagingBlock(account);

                    //更新101个受托人，已经出块人的状态
                    trusteeHandler.changeStatus(trustee, blockCycle);

                    logger.info("第{}个区块出块成功,出块账号:{}", newBlockHeight,account.getAddress());
                    provider.publishEvent(new GenerateNextBlockEvent(0L));
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                                provider.publishEvent(new GenerateNextBlockEvent(0L));
//                        }
//                    }).start();
                }
            }else {
                MonitoringIfProducerDead.nextBlockFlag = true;
            }
//        }catch (RocksDBException e){
//            e.printStackTrace();
//        }

    }


}
