package com.passport.db.dbhelper;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.Transaction;
import java.util.List;

/**
 *
 */
public interface DBAccess {


  boolean putLastBlockHeight(Object lastBlock);

  Optional<Object> getLastBlockHeight();


  boolean putBlock(Block block);


  Optional<Block> getBlock(Object blockHeight);


  Optional<Block> getLastBlock();


  Optional<List<String>> getNodeList();


  boolean putNodeList(List<String> nodes);


  boolean put(String key, Object value);


  Optional<Object> get(String key);


  boolean delete(String key);


  <T> List<T> seekByKey(String keyPrefix);


  List<Account> listAccounts();


  boolean putAccount(Account account);


  Optional<Account> getAccount(String address);


  boolean putUnconfirmTransaction(Transaction transaction);


  Optional<Transaction> getUnconfirmTransaction(String txHash);


  public List<Transaction> listUnconfirmTransactions();


  boolean putConfirmTransaction(Transaction transaction);


  Optional<Transaction> getConfirmTransaction(String txHash);

  Optional<Account> getMinerAccount();

  boolean putMinerAccount(Account account);
}
