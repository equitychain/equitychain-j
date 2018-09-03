package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("DATA_REQ_ACCOUNT_SYNC")
public class AccountSyncREQ extends Strategy {

  private static final Logger logger = LoggerFactory.getLogger(AccountSyncREQ.class);

  @Autowired
  private DBAccess dbAccess;

  public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {

    AccountMessage.Account account = message.getData().getAccount();
    Optional<Account> accountOptional = dbAccess.getAccount(String.valueOf(account.getAddress()));
    if (!accountOptional.isPresent()) {
      Account acc = accountOptional.get();
      if (dbAccess.putAccount(acc)) {
        logger.info("broadcast account {}success", acc.getAddress());
      }
    }
  }
}
