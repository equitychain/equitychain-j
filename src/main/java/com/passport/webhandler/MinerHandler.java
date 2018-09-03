package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.GsonUtils;
import com.passport.utils.eth.ByteUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Deal
 *
 * @author: Bee xu
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

  public void mining() {
    Optional<Block> lastBlock = dbAccess.getLastBlock();
    if (!lastBlock.isPresent()) {
      return;
    }
    Block prevBlock = lastBlock.get();

    BlockHeader currentBlockHeader = new BlockHeader();
    currentBlockHeader.setTimeStamp(System.currentTimeMillis());
    currentBlockHeader.setHashPrevBlock(prevBlock.getBlockHeader().getHash());

    currentBlockHeader.setEggMax(Long.parseLong("1000"));

    Optional<Account> minerAccountOptional = dbAccess.getMinerAccount();
    if (!minerAccountOptional.isPresent()) {
      return;
    }
    Account minerAccount = minerAccountOptional.get();

    Transaction transaction = new Transaction();
    transaction.setReceiptAddress(minerAccount.getAddress().getBytes());
    transaction.setValue(String.valueOf(new BigDecimal("10")).getBytes());
    transaction.setExtarData("dig reward".getBytes());
    transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));

    String transactionJson = GsonUtils.toJson(transaction);

    transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());

    List<Transaction> list = new ArrayList<>();
    list.add(transaction);

    Block currentBlock = new Block();
    currentBlock.setBlockHeader(currentBlockHeader);
    currentBlock.setBlockHeight(prevBlock.getBlockHeight() + 1);
    currentBlock.setTransactions(list);
    currentBlock.setTransactionCount(list.size());

    try {
      TimeUnit.SECONDS.sleep(3L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    List<Transaction> transactions = dbAccess.listUnconfirmTransactions();

    List<Transaction> blockTrans = transactionHandler
        .getBlockTrans(transactions, new BigDecimal(currentBlockHeader.getEggMax()));
    blockTrans.forEach((tran) -> {

      Transaction feeTrans = new Transaction();
      feeTrans.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
      feeTrans.setExtarData("dig reward".getBytes());
      BigDecimal valueDec = transactionHandler.getTempEggByHash(tran.getHash());
      valueDec = valueDec == null ? BigDecimal.ZERO : valueDec;
      feeTrans.setValue(String.valueOf(valueDec).getBytes());
      if (minerAccountOptional.isPresent()) {
        feeTrans.setReceiptAddress(minerAccountOptional.get().getAddress().getBytes());
      }

      currentBlock.getTransactions().add(feeTrans);
      currentBlock.getTransactions().add(tran);
    });

    transactionHandler.exec(currentBlock);

    dbAccess.putLastBlockHeight(prevBlock.getBlockHeight() + 1);
    currentBlock.calculateFieldValueWithHash();
    dbAccess.putBlock(currentBlock);

    provider.publishEvent(new SyncBlockEvent(currentBlock));
  }
}
