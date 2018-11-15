package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.annotations.RocksTransaction;
import com.passport.core.Account;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import com.passport.utils.SerializeUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务端处理账户同步请求
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_REQ_ACCOUNT_SYNC")//TODO 这里后期要优化为使用常量代替
public class AccountSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(AccountSyncREQ.class);

    @Autowired
    private BaseDBAccess dbAccess;

    @RocksTransaction
    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("处理账户同步请求数据：{}", GsonUtils.toJson(message));

        AccountMessage.Account account = message.getData().getAccount();
        Optional<Account> accountOptional = dbAccess.getAccount(String.valueOf(account.getAddress()));
        if(!accountOptional.isPresent()){
//            Account acc = accountOptional.get();
            Account acc = new Account();
            acc.setAddress(new String(account.getAddress().toByteArray()));
            acc.setBalance(BigDecimal.ZERO);
             if(dbAccess.putAccount(acc)){
                logger.info("接收广播账户{}成功", acc.getAddress());
                 InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                 String clientIP = insocket.getAddress().getHostAddress();
                 dbAccess.rocksDB.put( (clientIP+"_"+new String(account.getAddress().toByteArray())).getBytes(), SerializeUtils.serialize(new String(account.getAddress().toByteArray())));
                 dbAccess.rocksDB.put( (new String(account.getAddress().toByteArray())+"_"+clientIP).getBytes(),SerializeUtils.serialize(new String(account.getAddress().toByteArray())));
             }
        }
    }
}
