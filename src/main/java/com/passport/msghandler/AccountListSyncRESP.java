package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户端处理账户同步响应
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_RESP_ACCOUNTLIST_SYNC")//TODO 这里后期要优化为使用常量代替
public class AccountListSyncRESP extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(AccountListSyncRESP.class);

    @Autowired
    private DBAccess dbAccess;

    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理账户列表同步请求数据：{}", GsonUtils.toJson(message));

        List<AccountMessage.Account> accountsList = message.getData().getAccountsList();
        for (AccountMessage.Account account : accountsList) {
            Optional<Account> accountOptional = dbAccess.getAccount(account.getAddress().toString());
            if(!accountOptional.isPresent()){//同步不存在本地数据库的账户
                Account acc = new Account();
                acc.setAddress(new String(account.getAddress().toByteArray()));
                acc.setPrivateKey(new String(account.getPrivateKey().toByteArray()));
                byte[] balanceByte = account.getBalance().toByteArray();
                acc.setBalance((balanceByte==null||balanceByte.length==0)?BigDecimal.ZERO:new BigDecimal(new String(balanceByte)));
                boolean flag = dbAccess.putAccount(acc);
                if(flag){
                    logger.info("同步账户列表地址{}成功", acc.getAddress());
                }
            }
        }
    }
}
