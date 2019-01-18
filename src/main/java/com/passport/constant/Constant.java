package com.passport.constant;

import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.utils.DateUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
  int BLOCK_NODE_COUNT =1;
  ArrayBlockingQueue<List<Block>> BLOCK_QUEUE = new ArrayBlockingQueue(BLOCK_NODE_COUNT);

  //初始奖励金额
  BigDecimal BASE_REWARD = new BigDecimal("10");
  //金额衰减周期  高度
  int CYCLE = 100;
  //衰减倍数
  double multiple = 0.5;
  // 区块最多打包的流水数量
  int TRANS_SIZE = 100;

  Integer BLOCK_HEIGHT_GAP = 5;//本地区块和广播过来的区块高度差

  Integer BLOCK_SYNC_PERTIME = 2000;//一次主动同步的区块数

  Integer TRUSTEES_INIT_NUM = 11;//受托人初始化的人数

  Integer BLOCK_GENERATE_TIMEGAP = 2;//每个区块生成时间间隔

  Long GENESIS_BLOCK_TIMESTAMP = DateUtils.formatStringDate("2018-09-01 00:00:00").getTime();//创世区块时间

  String GENESIS_PATH = "./genesis.json";//创世文件

  BigDecimal FEE_4_REGISTER_TRUSTEE = new BigDecimal(1);//注册成为受托人资金

  BigDecimal FEE_4_REGISTER_VOTER = new BigDecimal(1);//注册成为投票人资产

  Integer CHANCE_4_VOTER = 10;//投票人总投票数

  Integer TRANS_EGG_MAXDEFALT = 50;//流水默认maxegg

  Integer TRANS_EGG_PRICEDEFALT = 2;//流水默认eggprice

  Integer BLOCK_DISTANCE = 3000000;//按10秒出一个区块，一个出块量360*24*365，一年约产生3000000个区块
  //每年出块奖励递减
  BigDecimal[] REWARD_ARRAY = {new BigDecimal(10), new BigDecimal(8), new BigDecimal(6), new BigDecimal(4), new BigDecimal(2), new BigDecimal(1)};
  //索引set集合分割大小
  int INDEX_GROUPSIZE = 1000;
  //主币
  String MAIN_COIN = "EQU";
  //位数
  BigDecimal SYMBOL = new BigDecimal(6);
  //投票人奖励流水判断标记
  String VOTER_TRANS_PROPORTION_EXTAR_DATA = "voter trans proportion extarData";
  //投票人奖励比例
  BigDecimal CONFIRM_TRANS_PROPORTION = new BigDecimal("0.2");
  //投票人奖励精度
  int PROPORTION_ACCURACY = 8;
  //主节点资产
  BigDecimal masterBalance = new BigDecimal(6.18*10000*10000);
}
