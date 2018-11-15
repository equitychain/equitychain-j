package com.passport.msghandler;

import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.core.Account;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.*;
import com.passport.utils.DataFormatUtil;
import com.passport.utils.GsonUtils;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

@Component("DATA_REQ_ACCOUNTIP_SYNC")
public class AccountIpSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(AccountIpSyncREQ.class);
    @Autowired
    BaseDBAccess dbAccess;
    @Autowired
    StoryFileUtil storyFileUtil;
    @Override
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("处理账户ip同步请求数据：{}", GsonUtils.toJson(message));
        //存对方IP地址
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        List<AccountMessage.Account> accounts = message.getData().getAccountsList();
        for (AccountMessage.Account account : accounts) {
            dbAccess.rocksDB.put( (clientIP+"_"+new String(account.getAddress().toByteArray())).getBytes(),SerializeUtils.serialize(new String(account.getAddress().toByteArray())));
            dbAccess.rocksDB.put( (new String(account.getAddress().toByteArray())+"_"+clientIP).getBytes(),SerializeUtils.serialize(new String(account.getAddress().toByteArray())));
        }
        //发送本地账户
        Set<String> localAddress = storyFileUtil.getAddresses();
        NettyData.Data.Builder dataBuilder1 = NettyData.Data.newBuilder();
        dataBuilder1.setDataType(DataTypeEnum.DataType.ACCOUNTIP_SYNC);
        for (String address : localAddress) {
            AccountMessage.Account.Builder builder1 = AccountMessage.Account.newBuilder();
            builder1.setAddress(ByteString.copyFrom(address.getBytes()));
            dataBuilder1.addAccounts(builder1.build());
        }
        NettyMessage.Message.Builder builder1 = NettyMessage.Message.newBuilder();
        builder1.setData(dataBuilder1.build());
        builder1.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
        ctx.writeAndFlush(builder1.build());
    }
}
