package com.passport.wallet;

import static com.passport.wallet.WalletTest.WALLET_DIR;

import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.Keys;
import com.passport.crypto.eth.WalletUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @since 18-7-14
 */
public class CredentialTest {

  static Logger logger = LoggerFactory.getLogger(CredentialTest.class);


  @Test
  public void createByPrivateKey() throws Exception {

    String privateKey = "bc3da6fa7ab05c21a1087e93206ce7635bc4be0a23340211174662441862217e";
    Credentials credentials = Credentials.create(privateKey);
    logger.info("ether address: " + credentials.getAddress());
    logger.info("privateKey: " + credentials.getEcKeyPair().exportPrivateKey());
  }


  @Test
  public void createByKeypair() throws Exception {

    ECKeyPair keyPair = Keys.createEcKeyPair();
    Credentials credentials = Credentials.create(keyPair);
    logger.info("ether address: " + credentials.getAddress());
    logger.info("privateKey: " + credentials.getEcKeyPair().exportPrivateKey());
  }

  @Test
  public void loadCredentialsFromWallet() throws Exception {

    String walletFile = WALLET_DIR
        + "/UTC--2018-08-01T06-35-14.976000000Z--bx9c65c2f348d0fb5f6896adfbf8e40309604f4a56.json";
    Credentials credentials = WalletUtils
        .loadCredentials("e10adc3949ba59abbe56e057f20f883e", walletFile);
    logger.info("ether address: " + credentials.getAddress());
    logger.info("privateKey: " + credentials.getEcKeyPair().exportPrivateKey());
  }


  @Test
  public void loadCredentialsFromMemorizingWords() throws Exception {
    //educate bread attract theme obey squirrel busy food finish segment sell audit
    //0xce7d01da2b1cfe5b65f35924127fa8f746a00050
    String memorizingWords = "educate bread attract theme obey squirrel busy food finish segment sell audit";
    Credentials credentials = WalletUtils.loadBip39Credentials(memorizingWords);
    logger.info("ether address: " + credentials.getAddress());
    logger.info("privateKey: " + credentials.getEcKeyPair().exportPrivateKey());
  }

  /**
   * test datas: memorizing word: worth flush raise credit unable very easily edge near nuclear
   * video vicious address: 0x7154dbe7a2f9f1a9632f886201efdf996627b387 password: 123456
   */
  @Test
  public void loadCredentialsWithWordsAndPass() throws Exception {

    String words = "worth flush raise credit unable very easily edge near nuclear video vicious";
    String password = "123456";

    Credentials credentials = WalletUtils.loadBip39Credentials(password, words);
    logger.info("ether address: " + credentials.getAddress());
    logger.info("privateKey: " + credentials.getEcKeyPair().exportPrivateKey());
  }
}
