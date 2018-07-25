package com.passport.core;

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
}
