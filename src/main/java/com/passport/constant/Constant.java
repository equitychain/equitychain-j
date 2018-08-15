package com.passport.constant;

import com.passport.core.Block;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Wu Created by SKINK on 2018/6/22.
 */
public interface Constant {

  Integer BYTE_HASH_LENGTH = 32;
  Integer STRING_HASH_LENGTH = 64;

  Integer BYTE_ADDRESS_LENGTH = 20;
  Integer STRING_ADDRESS_LENGTH = 40;

  //用来存储各个节点同步过来的区块
  //todo 这个队列是个有限队列，用来判断几个节点是否满了，至于多少个，需要更改
  int blockNodeCount = 2;
  ArrayBlockingQueue<List<Block>> blockQueue = new ArrayBlockingQueue(blockNodeCount);

  //初始奖励金额
  BigDecimal baseReward = new BigDecimal("100");
  //金额衰减周期  高度
  int cycle = 100;
  //衰减倍数
  double multiple = 0.5;

  String TEST = "asd";

}
