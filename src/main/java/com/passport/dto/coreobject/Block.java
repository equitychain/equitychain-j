package com.passport.dto.coreobject;

import java.util.List;

/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
public class Block {
  private Long blockSize;//区块大小
  BlockHeader blockHeader;//区块头信息
  private Integer transactionCount;//交易流水数量
  List<Transaction> transactions;//交易流水
  private Long blockHeight;
  private String producer;

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

  public String getProducer() {
    return producer;
  }

  public void setProducer(String producer) {
    this.producer = producer;
  }
}
