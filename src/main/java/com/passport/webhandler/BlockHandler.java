package com.passport.webhandler;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.MerkleTree;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class BlockHandler {
    private static final Logger logger = LoggerFactory.getLogger(BlockHandler.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    //用来存储各个节点同步过来的区块
    //todo 这个队列是个有限队列，用来判断几个节点是否满了，至于多少个，需要更改
    protected final ArrayBlockingQueue<List<Block>> blockQueue = new ArrayBlockingQueue(2);
    protected boolean padding = false;
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
            boolean flag = Objects.equal(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
            if(!flag){
                return false;
            }

            Optional<Block> prevBlock = dbAccess.getBlock(blockHeight - 1);
            if(prevBlock.isPresent()){
                byte[] hashOfPrevBlock = prevBlock.get().getBlockHeader().getHash();//前一个区块hash
                byte[] prevHashOfCurrentBlock = block.getBlockHeader().getHashPrevBlock();//当前区块的前一个区块hash
                if (hashOfPrevBlock.equals(prevHashOfCurrentBlock)) {//前一个区块的hash和当前区块的前一个区块hash是否相等
                    return true;
                }
            }
        }

        return false;
    }
    public synchronized void addBlockQueue(List<Block> blocks) throws InterruptedException {
        if(!padding) {
            //排序，根据高度排序 排序后再添加
            blocks.sort((block1,block2)->{
                return block1.getBlockHeight().compareTo(block2.getBlockHeight());
            });
            List<Block> checkList = new ArrayList<>();
            blockQueue.offer(blocks);
            boolean add = blockQueue.offer(checkList);
            if(!add){
                //满了，进行校验
                padding = true;
                //异步处理,不然其他的都在处于等待
                synHandlerBlock();
            }else{
                blockQueue.remove(checkList);
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

                    //todo 校验  然后如果各个节点的区块高度不一致,则同步最低的那个
                    Iterator<List<Block>> iterator = blockQueue.iterator();
                    List<List<Block>> blockList = new ArrayList<>();
                    int blockSize = -1;
                    while (iterator.hasNext()){
                        List<Block> blocks = iterator.next();
                        if(blockSize == -1 || blockSize > blocks.size()){
                            blockSize = blocks.size();
                        }
                        blockList.add(blocks);
                    }
                    //存储可以同步的区块
                    List<Block> successBlocks = new ArrayList<>();
                    // 遍历区块高度
                    for(int i = 0; i < blockSize; i++){
                        boolean isCheck = true;
                        byte[] markRoot = null;
                        byte[] hash = null;
                        //遍历各节点的该高度的区块
                        for(int j = 0; j < blockList.size(); j ++) {
                            Block block = blockList.get(j).get(i);
                            //校验markRoot
                            MerkleTree tree = new MerkleTree(block.getTransactions());
                            List<byte[]> treeBuild = tree.buildMerkleTree();
                            byte[] calTree = treeBuild.get(treeBuild.size()-1);
                            if(!Arrays.equals(calTree,block.getBlockHeader().getHashMerkleRoot())){
                                //markRoot校验不通过
                                return;
                            }
                            //校验节点间的数据是否吻合
                            if(j == 0){
                                markRoot = block.getBlockHeader().getHashMerkleRoot();
                                hash = block.getBlockHeader().getHash();
                            }else{
                                if(!Arrays.equals(markRoot,block.getBlockHeader().getHashMerkleRoot()) || !Arrays.equals(hash,block.getBlockHeader().getHash())){
                                    //节点间的校验不通过
                                    isCheck = false;
                                    break;
                                }

                            }
                        }
                        if(isCheck){
                            successBlocks.add(blockList.get(0).get(i));
                        }else{
                            //todo 剩下的高度不进行添加 这里是break呢还是return？
                            break;
                        }
                    }
                    //存储区块到本地
                    for(Block blockLocal : successBlocks) {
                        dbAccess.putBlock(blockLocal);
                        dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());
                    }
                    //继续同步下组区块
                    provider.publishEvent(new SyncNextBlockEvent(0L));
                }catch (Exception e){
                    logger.warn("synchronization block error", e);
                }finally {
                    //更改状态
                    padding = false;
                }
            }
        });
        handlerThread.start();
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
        blockHeaderBuilder.setHashPrevBlock(ByteString.copyFrom(block.getBlockHeader().getHashMerkleRoot()));
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
