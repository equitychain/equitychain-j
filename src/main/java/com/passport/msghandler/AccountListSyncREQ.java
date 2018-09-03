package com.passport.msghandler;

import com.google.protobuf.ByteString;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("DATA_REQ_ACCOUNTLIST_SYNC")
public class AccountListSyncREQ extends Strategy {

  private static final Logger logger = LoggerFactory.getLogger(AccountListSyncREQ.class);

  @Autowired
  private DBAccess dbAccess;

  public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {

    List<Account> accounts = dbAccess.listAccounts();

    NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
    dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNTLIST_SYNC);
    for (Account account : accounts) {
      AccountMessage.Account.Builder builder = AccountMessage.Account.newBuilder();
      builder.setAddress(ByteString.copyFrom(account.getAddress().getBytes()));
      builder.setPrivateKey(ByteString.copyFrom(account.getPrivateKey().getBytes()));
      builder.setBalance(ByteString.copyFrom(String.valueOf(account.getBalance()).getBytes()));
      dataBuilder.addAccounts(builder.build());
    }

    NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
    builder.setData(dataBuilder.build());
    builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
    System.out.println(builder.getData().getAccountsList().size());

    ctx.writeAndFlush(builder.build());
  }
}
