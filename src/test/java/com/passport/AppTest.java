package com.passport;

import com.alibaba.fastjson.JSONObject;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.BaseDBRocksImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rocksdb.*;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
        String s = "bxbc2f5dce6cef11940c0a6aa3be8f9c5c5e051adb" +","+
                "bx3251404046f549a3bc59a464e9741fd5ff2f29ff" +","+
                "bx4796d64c76a906036dc236a00244a445405fb9a1" +","+
                "bxe5b5b430b1eae095bc53ef3f2015ff97e9bdb5b6" +","+
                "bx21ac6fa86937118f1328eb37d13b3fbc3d18dda9" +","+
                "bx5282c0810a816e695ffec4b42daef312d508571a" +","+
                "bx4c436f809079db968cab51eb1c507277a2552573" +","+
                "bxa87098c75171e4fdd61d80174f45f7055a7cfa99" +","+
                "bx1369b40688b8ab65733c3b02b9e684fc59e3f6cb" +","+
                "bx1d389aef1b50b4da5b78b5d096abedaf13cb240e" +","+
                "bxc8dc925dee0d8f37426453b9fda1dc1dfbcdeea5" +","+
                "bx83d9f70a012134fcf44bec197da3f22c13c18671"
                ;
        List<String> list = Arrays.asList(s.split(","));
        for(int i = 0;i<list.size();i++){
            System.out.println(list.get(i));
        }
    }
}
