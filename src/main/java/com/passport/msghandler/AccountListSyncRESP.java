package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
}
