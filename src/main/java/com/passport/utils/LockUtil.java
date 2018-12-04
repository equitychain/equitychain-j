package com.passport.utils;

import com.passport.core.Account;
import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.Wallet;
import com.passport.crypto.eth.WalletFile;
import com.passport.crypto.eth.WalletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LockUtil {
    private static Logger logger = LoggerFactory.getLogger(LockUtil.class);
    protected static final long DEFAULT_LOCKTIME = 60*1000;
    private LockUtil(){

    }
    protected static Map<String,Long> addrLockMap = new HashMap<>();

    public static boolean unLockAddr(String address, String password, StoryFileUtil storyFileUtil){
        return unLockAddr(address,password,storyFileUtil,null);
    }

    public static boolean unLockAddr(String address, String password, StoryFileUtil storyFileUtil,Long lockTime){
        try {
            WalletFile file = storyFileUtil.getAddressInfo(address);
            if(file == null) return false;
            Credentials credentials = Credentials.create(Wallet.decrypt(password, file));
            String privateKey = credentials.getEcKeyPair().exportPrivateKey();
            if(privateKey != null && !"".equals(privateKey)) {
                long lock = DateUtils.getWebTime();
                if (lockTime != null) {
                    lock = lock + lockTime;
                } else {
                    lock = lock + DEFAULT_LOCKTIME;
                }
                addrLockMap.put(address, lock);
                return true;
            }
        }catch (Exception e){
            logger.error("unlock account exception",e);
        }
        return false;
    }

    public static boolean isUnlock(String address){
        Long time = addrLockMap.get(address);
        if(time == null) return false;
        long curTime = System.currentTimeMillis();
        return time - curTime >= 500;
    }
}
