package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
<<<<<<< HEAD
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.DataFormatUtil;
import com.passport.utils.GsonUtils;
import com.passport.utils.SerializeUtils;
import io.netty.channel.ChannelHandlerContext;
=======
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import java.math.BigDecimal;
import java.util.List;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.net.InetSocketAddress;
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
    private BaseDBAccess dbAccess;

    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("处理账户列表同步请求数据：{}", GsonUtils.toJson(message));
        List<AccountMessage.Account> accountsList = message.getData().getAccountsList();
        for (AccountMessage.Account account : accountsList) {
            Optional<Account> accountOptional = dbAccess.getAccount(DataFormatUtil.byteStringToString(account.getAddress())+"_"+DataFormatUtil.byteStringToString(account.getToken()));
            if(!accountOptional.isPresent()){//同步不存在本地数据库的账户
                Account acc = new Account();
                acc.setAddress_token(new String(account.getAddress().toByteArray())+"_"+new String(account.getToken().toByteArray()));
                acc.setPrivateKey(new String(account.getPrivateKey().toByteArray()));
                byte[] balanceByte = account.getBalance().toByteArray();
                acc.setBalance((balanceByte==null||balanceByte.length==0)?BigDecimal.ZERO:new BigDecimal(new String(balanceByte)));
                boolean flag = dbAccess.putAccount(acc);
                if(flag){
                    logger.info("同步账户列表地址{}成功", acc.getAddress_token());
               }
            }
        }
    }
=======

@Component("DATA_RESP_ACCOUNTLIST_SYNC")
public class AccountListSyncRESP extends Strategy {

  private static final Logger logger = LoggerFactory.getLogger(AccountListSyncRESP.class);

  @Autowired
  private DBAccess dbAccess;

  public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {

    List<AccountMessage.Account> accountsList = message.getData().getAccountsList();
    for (AccountMessage.Account account : accountsList) {
      Optional<Account> accountOptional = dbAccess.getAccount(account.getAddress().toString());
      if (!accountOptional.isPresent()) {
        Account acc = new Account();
        acc.setAddress(String.valueOf(account.getAddress()));
        acc.setPrivateKey(String.valueOf(account.getPrivateKey().toString()));
        acc.setBalance(new BigDecimal(String.valueOf(account.getBalance())));
        boolean flag = dbAccess.putAccount(acc);
        if (flag) {
          logger.info("account{}success", acc.getAddress());
        }
      }
    }
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
