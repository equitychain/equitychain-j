package com.passport;

import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.BaseDBRocksImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;

/**
 * Unit test for simple App.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception
    {
        /*File file = new File("./keystore");
        if(!file.exists()){
            file.mkdir();
        }
        System.out.println(file.getPath());
        ECKeyPair keyPair = WalletUtils.generateNewWalletFile("hq123456", file, true);
        Account account = new Account(keyPair.getAddress(), keyPair.exportPrivateKey(), BigDecimal.ZERO);
        System.out.println(GsonUtils.toJson(account));

        Credentials credentials = Credentials.create(keyPair.exportPrivateKey());
        System.out.println(credentials.getAddress());*/
//1536740510951     3600000
//        long valK = 3600500;
//        long gropSize = 3600*1000;
//        valK = valK/gropSize;
//        valK = valK*gropSize;
//        System.out.println(valK);
//        OptimisticTransactionDB db = OptimisticTransactionDB.open(new Options().setCreateIfMissing(true),"d:/test");
        RocksDB rocksDB = RocksDB.open(new Options().setCreateIfMissing(true),"d:/test");
        WriteBatch writeBatch1 = new WriteBatch();
//        Transaction transaction = db.beginTransaction(new WriteOptions());
        System.out.println();
    }
}
