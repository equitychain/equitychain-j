package com.passport.webhandler;

import com.google.common.base.Optional;
<<<<<<< HEAD
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.crypto.eth.Bip39Wallet;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.enums.TransactionTypeEnum;
import com.passport.exception.CipherException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.GsonUtils;
import com.passport.utils.HttpUtils;
import com.passport.utils.SerializeUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.StringUtil;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
=======
import com.passport.core.Account;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.exception.CipherException;
import com.passport.listener.ApplicationContextProvider;
import java.io.File;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

@Component
public class AccountHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccountHandler.class);

    @Autowired
    private BaseDBAccess dbAccess;
    //广播event用的
    @Autowired
    private ApplicationContextProvider provider;

    @Value("${wallet.keystoreDir}")
    private String walletDir;

    @Autowired
    private TransactionHandler transactionHandler;


    /**
     * 新增账号
     *
     * @return 账号
     */
    public Account newAccount(String password) throws Exception {
        Account account = generateAccount(password,"guest");
        if (dbAccess.putAccount(account)) {
            return account;
        }
        return null;
    }
    private Account generateAccount(String password,String identity) throws Exception {
        File file = new File(walletDir);
        if (!file.exists()) {
            file.mkdir();
        }

        //创建公私钥并生成keystore文件
//        ECKeyPair keyPair = WalletUtils.generateNewWalletFile(password, new File(walletDir), true);

        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, new File(walletDir));
        ECKeyPair keyPair = bip39Wallet.getKeyPair();
        Account account = new Account(keyPair.getAddress()+"_"+Constant.MAIN_COIN, keyPair.exportPrivateKey(), BigDecimal.ZERO,keyPair.getAddress(),Constant.MAIN_COIN,identity);
        account.setPassword(password);
        account.setMnemonic(bip39Wallet.getMnemonic());
        return account;
    }

    /**
     * 初始化受托人
     */
    public void generateTrustees() {
        GenesisBlockInfo genesisBlockInfo = new GenesisBlockInfo();
        try {
        List<Account> accounts = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        List<Trustee> trustees = new ArrayList<>();
        List<VoteRecord> voteRecords = new ArrayList<>();
        int voteNum = 1;
        for (int i = 0; i < Constant.TRUSTEES_INIT_NUM; i++) {

//          //TODO: 前30个节点各有10000票
//          if(i<=30){
//             voteNum = 10000;
//          }else{
//             voteNum = 1;
//          }
            //创建账户
            Account account = generateAccount("123456","guest");
//          if(i == 0){
//             account.setBalance(Constant.masterBalance);
//          }
            dbAccess.putAccount(account);
            String[] addressToken = account.getAddress_token().split("_");

            accounts.add(new Account(account.getAddress_token(),null, account.getBalance(),addressToken[0],addressToken[1],account.getIdentity()));//不保存私钥

            //创建注册为受托人交易
            Transaction transaction = transactionHandler.generateTransaction(addressToken[0], null, "0", "", account,addressToken[1]);
            transaction.setTradeType(TransactionTypeEnum.TRUSTEE_REGISTER.toString().getBytes());
            transaction.setBlockHeight("1".getBytes());
            transactions.add(transaction);

            //增加投票记录
            VoteRecord voteRecord = new VoteRecord();
            voteRecord.setPayAddress("");
            voteRecord.setReceiptAddress(addressToken[0]);
            voteRecord.setTime(Constant.GENESIS_BLOCK_TIMESTAMP);
            voteRecord.setStatus(1);
            voteRecord.setVoteNum(voteNum);
            voteRecord.setId();
            voteRecords.add(voteRecord);

            //把新增的受托人放到受托人列表
            Trustee trustee = new Trustee(addressToken[0], 1L, 0f, new BigDecimal(0), 1,0);
            trustees.add(trustee);
        }
        //创建账户 官方中间账户 start
        Account account = generateAccount("123456","centre");
        dbAccess.putAccount(account);
        String[] addressToken = account.getAddress_token().split("_");
        accounts.add(new Account(account.getAddress_token(),null, account.getBalance(),addressToken[0],addressToken[1],account.getIdentity()));
        //end
        //创建账户 官方账户 start
        Account accountMaster = generateAccount("123456","master");
        accountMaster.setBalance(Constant.masterBalance);
        dbAccess.putAccount(accountMaster);
        String[] addressMasterToken = accountMaster.getAddress_token().split("_");
        accounts.add(new Account(accountMaster.getAddress_token(),null, accountMaster.getBalance(),addressMasterToken[0],addressMasterToken[1],accountMaster.getIdentity()));
        Transaction transaction = transactionHandler.generateTransaction("", addressMasterToken[0], Constant.masterBalance.toString(), "", accountMaster,addressToken[1]);
        transaction.setTradeType(TransactionTypeEnum.BLOCK_REWARD.toString().getBytes());
        transaction.setBlockHeight("1".getBytes());
        transactions.add(transaction);
        //end

        genesisBlockInfo.setAccounts(accounts);
        genesisBlockInfo.setTransactions(transactions);
        genesisBlockInfo.setTrustees(trustees);
        genesisBlockInfo.setVoteRecords(voteRecords);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //在项目下生成json文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.GENESIS_PATH))) {
            writer.write(GsonUtils.toJson(genesisBlockInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountHandler {

  private static final Logger logger = LoggerFactory.getLogger(AccountHandler.class);

  @Autowired
  private DBAccess dbAccess;


  @Autowired
  private ApplicationContextProvider provider;

  @Value("${wallet.keystoreDir}")
  private String walletDir;


  public Account newAccount(String password)
      throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
    File file = new File(walletDir);
    if (!file.exists()) {
      file.mkdir();
    }

    ECKeyPair keyPair = WalletUtils.generateNewWalletFile(password, new File(walletDir), true);
    Account account = new Account(keyPair.getAddress(), keyPair.exportPrivateKey(),
        BigDecimal.ZERO);
    account.setPassword(password);
    if (dbAccess.putAccount(account)) {
      return account;
    }
    return null;
  }

  public Account setMinerAccount(String address) {
    Optional<Account> addAccount = dbAccess.getAccount(address);
    if (addAccount.isPresent()) {
      Account account = addAccount.get();
      if (dbAccess.putMinerAccount(account)) {
        return account;
      }
    }
    return null;
  }

  public void setMinerAccountIfNotExists(Account account) {
    Optional<Account> minerAccount = dbAccess.getMinerAccount();
    if (!minerAccount.isPresent()) {
      dbAccess.putMinerAccount(account);
    }
  }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
