package com.passport;

import com.passport.core.Account;
import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.utils.GsonUtils;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception
    {
        File file = new File("./keystore");
        if(!file.exists()){
            file.mkdir();
        }
        System.out.println(file.getPath());
        ECKeyPair keyPair = WalletUtils.generateNewWalletFile("hq123456", file, true);
        Account account = new Account(keyPair.getAddress(), keyPair.exportPrivateKey(), BigDecimal.ZERO);
        System.out.println(GsonUtils.toJson(account));

        Credentials credentials = Credentials.create(keyPair.exportPrivateKey());
        System.out.println(credentials.getAddress());

    }
}
