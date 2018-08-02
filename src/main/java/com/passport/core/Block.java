package com.passport.core;

import com.passport.utils.rpc.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
public class Block {
  private static Logger logger = LoggerFactory.getLogger(Block.class);

  private Long blockSize;//区块大小
  BlockHeader blockHeader;//区块头信息
  private Integer transactionCount;//交易流水数量
  List<Transaction> transactions;//交易流水
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
}
