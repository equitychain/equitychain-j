package com.passport.core;

import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import com.passport.utils.rpc.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
@EntityClaz(name = "block")
public class Block {
  private static Logger logger = LoggerFactory.getLogger(Block.class);
  @FaildClaz(name = "blockSize",type = Long.class)
  private Long blockSize;//区块大小
  @FaildClaz(name = "blockHeader",type = BlockHeader.class)
  BlockHeader blockHeader;//区块头信息
  @FaildClaz(name = "transactionCount",type = Integer.class)
  private Integer transactionCount;//交易流水数量
  @FaildClaz(name = "transactions",type = List.class,genericParadigm = Transaction.class)
  List<Transaction> transactions;//交易流水
  @KeyField
  @FaildClaz(name = "blockHeight",type = Long.class)
  private Long blockHeight;

  public static Logger getLogger() {
    return logger;
  }

  public static void setLogger(Logger logger) {
    Block.logger = logger;
  }

  public Long getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(Long blockSize) {
    this.blockSize = blockSize;
  }

  public BlockHeader getBlockHeader() {
    return blockHeader;
  }

  public void setBlockHeader(BlockHeader blockHeader) {
    this.blockHeader = blockHeader;
  }

  public Integer getTransactionCount() {
    return transactionCount;
  }

  public void setTransactionCount(Integer transactionCount) {
    this.transactionCount = transactionCount;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public Long getBlockHeight() {
    return blockHeight;
  }

  public void setBlockHeight(Long blockHeight) {
    this.blockHeight = blockHeight;
  }

  //计算block对象缺少的字段值,且返回区块hash
  public byte[] calculateFieldValueWithHash(){
    //生成merkleTree
    MerkleTree merkleTree = new MerkleTree(transactions);
    List<byte[]> bytes = merkleTree.buildMerkleTree();
    //设置merkleRoot
    if(bytes.size() > 0){
      blockHeader.setHashMerkleRoot(bytes.get(bytes.size() - 1));
    }

    //生成区块hash
    blockHeader.calculateHash();

    //计算区块大小(除blockSize都需要计算)
    Block block = new Block();
    block.setBlockHeader(this.getBlockHeader());
    block.setTransactionCount(this.getTransactionCount());
    block.setTransactions(this.getTransactions());
    block.setBlockHeight(this.getBlockHeight());
    byte[] blockByte = SerializationUtil.serialize(block);
    this.blockSize = Long.valueOf(blockByte.length);

    return blockHeader.getHash();
  }
  public boolean isNullContent(){
    return blockSize == null && blockHeader == null
            && transactionCount==null&& transactions==null;
  }
  //之所以复写hashCode和equals，是因为list的contans方法，blockHandler里面的检查区块用到了
  @Override
  public int hashCode() {
    return (int)(blockHeight%Integer.MAX_VALUE);
  }

  @Override
  public boolean equals(Object obj) {
    if(obj==null || !(obj instanceof Block)){
      return false;
    }
    Block block = (Block)obj;
    return this.blockHeight == block.blockHeight
            && Arrays.equals(this.blockHeader.getHash(),block.blockHeader.getHash())
            && Arrays.equals(this.blockHeader.getHashMerkleRoot(),block.blockHeader.getHashMerkleRoot())
            && Arrays.equals(this.blockHeader.getHashPrevBlock(),block.blockHeader.getHashPrevBlock());
  }
}
