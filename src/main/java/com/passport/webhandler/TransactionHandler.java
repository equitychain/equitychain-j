package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.Sign;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.enums.ResultEnum;
import com.passport.event.SendTransactionEvent;
import com.passport.exception.CommonException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.utils.eth.ByteUtil;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TransactionHandler {

  private static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);
  @Autowired
  private DBAccess dbAccess;
  @Autowired
  private ApplicationContextProvider provider;

  private HashMap<byte[], BigDecimal> eggUsedTemp = new HashMap<>();

  public Transaction sendTransaction(String payAddress, String receiptAddress, String value,
      String extarData, String password) throws CommonException {

    //if(LockUtil.isUnlock(payAddress)) {
    if (true) {
      if (!WalletUtils.isValidAddress(payAddress) && !WalletUtils.isValidAddress(receiptAddress)) {
        throw new CommonException(ResultEnum.ADDRESS_ILLEGAL);
      }

      Optional<Account> accountPayOptional = dbAccess.getAccount(payAddress);
      Optional<Account> accountReceiptOptional = dbAccess.getAccount(receiptAddress);
      if (!accountPayOptional.isPresent()) {
        throw new CommonException(ResultEnum.PASSWORD_WRONG);
      }
      Account accountPay = accountPayOptional.get();
      if (!password.equals(accountPay.getPassword())) {
        throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
      }

            /*if(!accountReceiptOptional.isPresent()){
                throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
            }*/




            /*if(accountPay.getBalance().compareTo(CastUtils.castBigDecimal(value)) == -1){
                throw new CommonException(ResultEnum.BALANCE_NOTENOUGH);
            }*/

      Transaction transaction = generateTransaction(payAddress, receiptAddress, value, extarData,
          accountPay);

      provider.publishEvent(new SendTransactionEvent(transaction));
      return transaction;
    } else {
      throw new CommonException(ResultEnum.ACCOUNT_IS_LOCKED);
    }
  }


  public Transaction generateTransaction(String payAddress, String receiptAddress, String value,
      String extarData, Account accountPay) {
    Transaction transaction = new Transaction();
    transaction.setPayAddress(payAddress.getBytes());
    transaction.setReceiptAddress(receiptAddress.getBytes());
    transaction.setValue(value.getBytes());
    transaction.setExtarData(extarData.getBytes());
    transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));

    String transactionJson = GsonUtils.toJson(transaction);
    try {

      PrivateKey privateKey = Sign.privateKeyFromString(accountPay.getPrivateKey());
      transaction.setSignature(ECDSAUtil.applyECDSASig(privateKey, transactionJson));

      Credentials credentials = Credentials.create(accountPay.getPrivateKey());
      transaction.setPublicKey(credentials.getEcKeyPair().getPublicKey().getEncoded());
    } catch (Exception e) {
      throw new CommonException(ResultEnum.SYS_ERROR);
    }
    transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());

    return transaction;
  }


  public void exec(Block currentBlock) {

    eggUsedTemp.clear();
    for (Transaction transaction : currentBlock.getTransactions()) {
      String receiptAddress = new String(transaction.getReceiptAddress());
      byte[] payAddressByte = transaction.getPayAddress();
      BigDecimal valueBigDecimal = CastUtils.castBigDecimal(new String(transaction.getValue()));

      Optional<Account> receiptOptional = dbAccess.getAccount(receiptAddress);
      if (!receiptOptional.isPresent()) {
        receiptOptional = Optional.of(new Account(receiptAddress, BigDecimal.ZERO));
      }
      Account accountReceipt = receiptOptional.get();

      if (payAddressByte == null) {
        accountReceipt.setBalance(accountReceipt.getBalance().add(valueBigDecimal));
        dbAccess.putAccount(accountReceipt);
        continue;
      }
      String payAddress = new String(payAddressByte);

      Transaction trans = new Transaction();
      trans.setPayAddress(transaction.getPayAddress());
      trans.setReceiptAddress(transaction.getReceiptAddress());
      trans.setValue(transaction.getValue());
      trans.setExtarData(transaction.getExtarData());
      trans.setTime(transaction.getTime());
      String transactionJson = GsonUtils.toJson(trans);
      try {
        boolean flag = Sign
            .verify(transaction.getPublicKey(), new String(transaction.getSignature()),
                transactionJson);
        if (!flag) {
          logger.info("transactions sign error");
          continue;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      Optional<Account> payOptional = dbAccess.getAccount(payAddress);
      Account accountPay = payOptional.get();

      if (accountPay.getBalance().compareTo(valueBigDecimal) == -1) {
        logger.info("enough money");
        continue;
      }

      accountPay.setBalance(accountPay.getBalance().subtract(valueBigDecimal));
      accountReceipt.setBalance(accountReceipt.getBalance().add(valueBigDecimal));
      dbAccess.putAccount(accountPay);
      dbAccess.putAccount(accountReceipt);

    }
  }


  public List<Transaction> getBlockTrans(List<Transaction> unconfirmTrans, BigDecimal blockMaxEgg) {
    List<Transaction> transactions = new ArrayList<>();
    Collections.sort(unconfirmTrans, new Comparator<Transaction>() {
      @Override
      public int compare(Transaction o1, Transaction o2) {
        BigDecimal price1 = o1 == null || o1.getEggPrice() == null ? BigDecimal.ZERO
            : new BigDecimal(new String(o1.getEggPrice()));
        BigDecimal price2 = o2 == null || o2.getEggPrice() == null ? BigDecimal.ZERO
            : new BigDecimal(new String(o2.getEggPrice()));
        return price2.compareTo(price1);
      }
    });
    for (Transaction tran : unconfirmTrans) {

      BigDecimal eggUsed = getEggUsedByTrans(tran);

      if (eggUsed.compareTo(BigDecimal.ZERO) > 0 && blockMaxEgg.compareTo(eggUsed) >= 0) {
        System.out.println("======add=======" + eggUsed);
        transactions.add(tran);
        eggUsedTemp.put(tran.getHash(), eggUsed);
        blockMaxEgg = blockMaxEgg.subtract(eggUsed);
        if (blockMaxEgg.compareTo(BigDecimal.ZERO) == 0) {
          break;
        }
      }
    }
    return transactions;
  }

  public BigDecimal getEggUsedByTrans(Transaction transaction) {
    long begin = System.currentTimeMillis();
    try {
      Thread.sleep(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    BigDecimal eggUsed = new BigDecimal(
        transaction.getEggUsed() == null ? "0" : new String(transaction.getEggUsed()));
    BigDecimal eggMax = new BigDecimal(
        transaction.getEggMax() == null ? "0" : new String(transaction.getEggMax()));
    BigDecimal curUse = new BigDecimal(end - begin);
    if (eggMax.compareTo(eggUsed.add(curUse)) >= 0) {
      transaction.setEggUsed(eggUsed.add(curUse).toString().getBytes());
      System.out.println("======test=======");
      return curUse;
    } else {
      return BigDecimal.ZERO;
    }
  }

  public BigDecimal getTempEggByHash(byte[] transHash) {
    return eggUsedTemp.get(transHash);
  }
}
