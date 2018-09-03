package com.passport.utils;

import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.Wallet;
import com.passport.crypto.eth.WalletFile;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockUtil {

  protected static final long DEFAULT_LOCKTIME = 60 * 1000;
  protected static Map<String, Long> addrLockMap = new HashMap<>();
  private static Logger logger = LoggerFactory.getLogger(LockUtil.class);

  private LockUtil() {

  }

  public static boolean unLockAddr(String address, String password, StoryFileUtil storyFileUtil) {
    return unLockAddr(address, password, storyFileUtil, null);
  }

  public static boolean unLockAddr(String address, String password, StoryFileUtil storyFileUtil,
      Long lockTime) {
    try {
      WalletFile file = storyFileUtil.getAddressInfo(address);
      if (file == null) {
        return false;
      }
      Credentials credentials = Credentials.create(Wallet.decrypt(password, file));
      String privateKey = credentials.getEcKeyPair().exportPrivateKey();
      if (privateKey != null && !"".equals(privateKey)) {
        long lock = System.currentTimeMillis();
        if (lockTime != null) {
          lock = lock + lockTime;
        } else {
          lock = lock + DEFAULT_LOCKTIME;
        }
        addrLockMap.put(address, lock);
        return true;
      }
    } catch (Exception e) {
      logger.error("unlock account exception", e);
    }
    return false;
  }

  public static boolean isUnlock(String address) {
    Long time = addrLockMap.get(address);
    if (time == null) {
      return false;
    }
    long curTime = System.currentTimeMillis();
    return time - curTime >= 500;
  }
}
