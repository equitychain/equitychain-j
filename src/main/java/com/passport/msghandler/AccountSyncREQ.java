package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端处理账户同步请求
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_REQ_ACCOUNT_SYNC")//TODO 这里后期要优化为使用常量代替
public class AccountSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(AccountSyncREQ.class);

    @Autowired
    private DBAccess dbAccess;

    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理账户同步请求数据：{}", GsonUtils.toJson(message));

        AccountMessage.Account account = message.getData().getAccount();
        Optional<Account> accountOptional = dbAccess.getAccount(String.valueOf(account.getAddress()));
        if(!accountOptional.isPresent()){
            Account acc = accountOptional.get();
             if(dbAccess.putAccount(acc)){
                logger.info("接收广播账户{}成功", acc.getAddress());
             }
        }
    }
}
