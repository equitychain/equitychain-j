package com.passport.msghandler;

import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

@Component("DATA_RESP_ACCOUNT_MINER")
public class AccountMinerRESP extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(AccountMinerRESP.class);
    @Autowired
    BaseDBAccess dbAccess;
    @Autowired
    StoryFileUtil storyFileUtil;
    @Override
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        //收到消息进行处理
        logger.info("收到启动出块请求");
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        //获取节点的账户列表
        List<String> ipAddress = dbAccess.seekByKey(clientIP);
        List<Trustee> trustees = dbAccess.listTrustees();
        for(String address:ipAddress){
            for(Trustee trustee : trustees){
                if(address.equals(trustee.getAddress())){
                    trustee.setState(1);
                    dbAccess.putTrustee(trustee);
                }
            }
        }
    }
}
