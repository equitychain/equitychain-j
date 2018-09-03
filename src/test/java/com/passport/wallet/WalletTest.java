package com.passport.wallet;

import com.passport.crypto.eth.Bip39Wallet;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import java.io.File;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 18-4-9
 */
public class WalletTest {

  public static final File WALLET_DIR = new File("./keystore");
  public static final String WALLET_PASS = "123456";
  static Logger logger = LoggerFactory.getLogger(WalletTest.class);

  static {
    if (!WALLET_DIR.exists()) {
      WALLET_DIR.mkdir();
    }
  }

  @Test
  public void generateNewWalletFile() throws Exception {

    //String filename = WalletUtils.generateFullNewWalletFile(WALLET_PASS, WALLET_DIR);
    //logger.info("wallet name: " + filename);
  }


  @Test
  public void generateLightNewWalletFile() throws Exception {
    //String filename = WalletUtils.generateLightNewWalletFile(WALLET_PASS, WALLET_DIR);
    ECKeyPair ecKeyPair = WalletUtils.generateNewWalletFile(WALLET_PASS, WALLET_DIR, true);

    //logger.info("wallet name: " + filename);
  }


  @Test
  public void generateBip39Wallet() throws Exception {

    Bip39Wallet wallet = WalletUtils.generateBip39Wallet();
    logger.info("memorizing word: " + wallet.getMnemonic());
    logger.info("address: " + wallet.getKeyPair().getAddress());
  }


  @Test
  public void generateBip39WalletFileWithPass() throws Exception {

    Bip39Wallet wallet = WalletUtils.generateBip39Wallet(WALLET_PASS, WALLET_DIR);
    logger.info("memorizing word: " + wallet.getMnemonic());
    logger.info("wallet file: " + wallet.getFilename());
    logger.info("address: " + wallet.getKeyPair().getAddress());
  }

  @Test
  public void generateBip39WalletWithPass() throws Exception {

    Bip39Wallet wallet = WalletUtils.generateBip39Wallet(WALLET_PASS);
    logger.info("memorizing word: " + wallet.getMnemonic());
    logger.info("address: " + wallet.getKeyPair().getAddress());
  }

}
