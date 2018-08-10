package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.exception.CipherException;
import com.passport.listener.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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

    /**
     * 新增账号
     * @return 账号
     */
    public Account newAccount(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        File file = new File(walletDir);
        if (!file.exists()) {
            file.mkdir();
        }

        //创建公私钥并生成keystore文件
        ECKeyPair keyPair = WalletUtils.generateNewWalletFile(password, new File(walletDir), true);
        Account account = new Account(keyPair.getAddress(), keyPair.exportPrivateKey(), BigDecimal.ZERO);
        account.setPassword(password);
        if (dbAccess.putAccount(account)) {
            return account;
        }
        return null;
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

}
