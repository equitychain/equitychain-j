package com.passport.wallet;

<<<<<<< HEAD
import com.alibaba.fastjson.JSON;
import com.passport.crypto.eth.*;
=======
import com.passport.crypto.eth.Bip39Wallet;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import java.io.File;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

<<<<<<< HEAD
import java.io.File;

/**
 * 生成钱包测试
 *
=======
/**
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
 * @since 18-4-9
 */
public class WalletTest {

<<<<<<< HEAD
    static Logger logger = LoggerFactory.getLogger(WalletTest.class);
    public static final File WALLET_DIR = new File("./keystore");
    public static final String WALLET_PASS = "123456";

    //初始化
    static {
        if (!WALLET_DIR.exists()) {
            WALLET_DIR.mkdir();
        }
    }

    /**
     * 生成默认普通钱包
     *
     * @throws Exception
     */
    @Test
    public void generateNewWalletFile() throws Exception {
/*
        String filename = WalletUtils.generateFullNewWalletFile(WALLET_PASS, WALLET_DIR);
		logger.info("wallet name: " + filename);*/
    }

    /**
     * 生成轻钱包， 轻钱包消耗更少的 CPU 和内存资源
     * Scrypt不仅计算所需时间长，而且占用的内存也多，使得并行计算多个摘要异常困难，因此利用 rainbow table进行暴力攻击更加困难。
     * 轻钱包没有对数据进行 full scrypt, 所以安全性会稍微低一些，但是性能高
     */
    @Test
    public void generateLightNewWalletFile() throws Exception {
        //String filename = WalletUtils.generateLightNewWalletFile(WALLET_PASS, WALLET_DIR);
        ECKeyPair ecKeyPair = WalletUtils.generateNewWalletFile(WALLET_PASS, WALLET_DIR, true);

        //logger.info("wallet name: " + filename);
    }

    /**
     * 使用助记词生成一个钱包
     */
    @Test
    public void generateBip39Wallet() throws Exception {

        Bip39Wallet wallet = WalletUtils.generateBip39Wallet();
        logger.info("memorizing word: " + wallet.getMnemonic());
        logger.info("address: " + wallet.getKeyPair().getAddress());
    }

    /**
     * 使用 (密码+助记词) 生成一个钱包文件, 在恢复钱包的时候可以使用两种方法
     * 1. 助记词 + 密码
     * 2. keystore file + 密码
     */
    @Test
    public void generateBip39WalletFileWithPass() throws Exception {

        Bip39Wallet wallet = WalletUtils.generateBip39Wallet(WALLET_PASS, WALLET_DIR);
        logger.info("memorizing word: " + wallet.getMnemonic());
        logger.info("wallet file: " + wallet.getFilename());
        logger.info("address: " + wallet.getKeyPair().getAddress());
    }

    /**
     * 使用（助记词 + 密码）生成钱包，意味着在恢复的钱包的时候，光有助记词还不够，还必须有密码
     */
    @Test
    public void generateBip39WalletWithPass() throws Exception {

        Bip39Wallet wallet = WalletUtils.generateBip39Wallet(WALLET_PASS);
        logger.info("memorizing word: " + wallet.getMnemonic());
        logger.info("address: " + wallet.getKeyPair().getAddress());
    }


    @Test
    public void name() throws Exception {
    }

    @Test
    //备份
    public void backup() throws Exception {
        String pwd = "123456789";
        String mnemonic = "";
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(pwd, mnemonic);
        String mnem = bip39Wallet.getMnemonic();
        ECKeyPair ecKeyPair = bip39Wallet.getKeyPair();
        WalletFile walletFile = Wallet.createLight(pwd, ecKeyPair);
        System.out.println("导出密钥：" + JSON.toJSON(walletFile));
    }

    //导入(密码+助记词)
    public void importWallet() throws Exception {
        String pwd = "123456789";
        String mnem = "";
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(pwd, mnem);
        String fileName = WalletUtils.generateWalletFile(pwd, bip39Wallet.getKeyPair(), WALLET_DIR, true);
    }

    //导入(密码+密钥文件)
    public void importWalletByFile() throws Exception {
        String pwd = "123456789";
        String fileJson = "";
        WalletFile walletFile = JSON.parseObject(fileJson, WalletFile.class);
        ECKeyPair ecKeyPair = Wallet.decrypt(pwd, walletFile);
        String fileName = WalletUtils.generateWalletFile(pwd, ecKeyPair, WALLET_DIR, true);
    }
=======
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

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
