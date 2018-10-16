package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.DataFormatUtil;
import com.passport.utils.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Component("DATA_RESP_ACCOUNTIP_SYNC")
public class AccountIpSyncRESP extends Strategy {
    @Autowired
    DBAccess dbAccess;
    @Override
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        //收到消息进行处理
        List<AccountMessage.Account> accountsList = message.getData().getAccountsList();
        List<Account> accounts = new ArrayList<>();
        for (AccountMessage.Account account : accountsList) {
            Account account1 = new Account();
            account1.setAddress(new String(account.getAddress().toByteArray()));
            accounts.add(account1);
        }
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        //保存地址ip信息
        dbAccess.saveIpAccountInfos(clientIP,accounts);
    }
}
