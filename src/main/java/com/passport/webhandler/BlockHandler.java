package com.passport.webhandler;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.constant.Constant;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.BlockHeaderMessage;
import com.passport.proto.BlockMessage;
import com.passport.proto.TransactionMessage;
import com.passport.utils.RawardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BlockHandler {
    private static final Logger logger = LoggerFactory.getLogger(BlockHandler.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    public volatile boolean padding = false;
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
            for(Transaction tran : transactions) {
                byte[] payAddr = tran.getPayAddress();
                byte[] compByt = "挖矿奖励".getBytes();
                if ((payAddr == null||payAddr.length==0) && tran.getExtarData() != null && Arrays.equals(compByt, tran.getExtarData())) {
                    //奖励的流水
                    byte[] value = tran.getValue();
                    reword = RawardUtil.checkReward(blockHeight, value);
                    break;
                }
            }
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
                try{
                    //todo 校验 目前是获取相同的区块高度
                    List<Block> successBlocks = getShareBlocks();
                    //存储区块到本地
                    for(Block blockLocal : successBlocks) {
                        Optional<Object> optHeigth = dbAccess.getLastBlockHeight();
                        if(optHeigth.isPresent()) {
                            Long height = (Long)optHeigth.get();
                            if(height != null) {
                                if((blockLocal.getBlockHeight() - height) == 1) {
                                    dbAccess.putBlock(blockLocal);
                                    dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());

                                    //同时保存区块中的流水到已确认流水列表中
                                    blockLocal.getTransactions().forEach(transaction -> {
                                        dbAccess.putConfirmTransaction(transaction);
                                    });
                                }
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                    //继续同步下组区块
                    provider.publishEvent(new SyncNextBlockEvent(0L));
                }catch (Exception e){
                    logger.warn("synchronization block error", e);
                }finally {
                    //更改状态
                    padding = false;
                    //清空队列
                    Constant.BLOCK_QUEUE.clear();
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

            blockBuilder.addTransactions(transactionBuilder.build());
        });

        return blockBuilder;
    }
}
