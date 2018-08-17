package com.passport.msghandler;

import com.google.protobuf.ByteString;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 服务端处理账户同步请求
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_REQ_ACCOUNTLIST_SYNC")//TODO 这里后期要优化为使用常量代替
public class AccountListSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(AccountListSyncREQ.class);

    @Autowired
    private DBAccess dbAccess;

    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理账户同步请求数据：{}", GsonUtils.toJson(message));

        List<Account> accounts = dbAccess.listAccounts();

        //返回请求的区块
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
