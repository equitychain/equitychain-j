package com.passport.msghandler;

import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.peer.ChannelsManager;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Component("DATA_RESP_HEART_BEAT")
public class HeartBeatRESP extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatRESP.class);
    @Autowired
    private ApplicationContextProvider provider;
    @Override
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.debug("处理心跳响应结果：{}", GsonUtils.toJson(message));
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        ChannelsManager.concurrentHashMap.put(inetSocketAddress.getAddress().getHostAddress(),0);
    }
}
