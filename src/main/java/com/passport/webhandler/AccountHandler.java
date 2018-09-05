package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.GenesisBlockInfo;
import com.passport.core.Transaction;
import com.passport.core.Trustee;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.exception.CipherException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccountHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccountHandler.class);

    @Autowired
    private DBAccess dbAccess;

    //广播event用的
    @Autowired
    private ApplicationContextProvider provider;

    @Value("${wallet.keystoreDir}")
    private String walletDir;

    @Autowired
    private TransactionHandler transactionHandler;

    /**
     * 新增账号
     * @return 账号
     */
    public Account newAccount(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        Account account = generateAccount(password);
        if (dbAccess.putAccount(account)) {
            return account;
        }
        return null;
    }

    private Account generateAccount(String password) throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        File file = new File(walletDir);
        if (!file.exists()) {
            file.mkdir();
        }

        //创建公私钥并生成keystore文件
        ECKeyPair keyPair = WalletUtils.generateNewWalletFile(password, new File(walletDir), true);
        Account account = new Account(keyPair.getAddress(), keyPair.exportPrivateKey(), BigDecimal.ZERO);
        account.setPassword(password);
        return account;
    }

    //用户替换挖矿账号
    public Account setMinerAccount(String address){
        Optional<Account> addAccount = dbAccess.getAccount(address);
        if(addAccount.isPresent()){
            Account account = addAccount.get();
            if(dbAccess.putMinerAccount(account)){
                return account;
            }
        }
        return null;
    }

    //用户设置默认挖矿账号（第一个创建的账户默认为挖矿账户）
    public void setMinerAccountIfNotExists(Account account){
        Optional<Account> minerAccount = dbAccess.getMinerAccount();
        if(!minerAccount.isPresent()){
            dbAccess.putMinerAccount(account);
        }
    }

    /**
     * 初始化受托人
     */
    public void generateTrustees(){
        GenesisBlockInfo genesisBlockInfo = new GenesisBlockInfo();
        List<Account> accounts = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        List<Trustee> trustees = new ArrayList<>();
        for (int i = 0; i < Constant.TRUSTEES_INIT_NUM; i++) {
            try {
                //创建账户
                Account account = generateAccount("123456");
                dbAccess.putAccount(account);
                accounts.add(new Account(account.getAddress(), account.getBalance()));//不保存私钥

                //创建注册为受托人交易
                Transaction transaction = transactionHandler.generateTransaction(account.getAddress(), null, "0", "", account);
                transactions.add(transaction);

                //把新增的受托人放到受托人列表
                Trustee trustee = new Trustee(account.getAddress(), 0L, 0f, new BigDecimal(0));
                trustees.add(trustee);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        genesisBlockInfo.setAccounts(accounts);
        genesisBlockInfo.setTransactions(transactions);
        genesisBlockInfo.setTrustees(trustees);
        //在项目下生成json文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.GENESIS_PATH))) {
            writer.write(GsonUtils.toJson(genesisBlockInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
