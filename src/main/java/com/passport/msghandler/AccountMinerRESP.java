package com.passport.msghandler;

import com.passport.constant.SyncFlag;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.AccountMessage;
import com.passport.proto.NettyMessage;
import com.passport.proto.TrusteeMessage;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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

        //获取节点的账户列表
        List<Trustee> trustees = dbAccess.listTrustees();
        List<String> localaddress = new ArrayList<>();
        List<TrusteeMessage.Trustee> msgTrustees = message.getData().getTrusteeList();
        for(Trustee trustee : trustees){
            for(TrusteeMessage.Trustee trus:msgTrustees){
                String address = new String(trus.getAddress().toByteArray());
                if(address.equals(trustee.getAddress())){
                    SyncFlag.waitMiner.put(address,1);
                    localaddress.add(address);
                }
            }
        }
        //收到消息进行处理
        logger.info("收到启动出块请求放入等待"+localaddress+"到下个周期才允许启动");
    }
}
