package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.exception.CipherException;
import com.passport.listener.ApplicationContextProvider;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

}
