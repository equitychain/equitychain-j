package com.passport.msghandler;

import com.google.protobuf.ByteString;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import com.passport.utils.StoryFileUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("DATA_RESP_TRUSTEE_SYNC")
public class TrusteeSyncRESP extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeSyncRESP.class);
    @Autowired
    BaseDBAccess dbAccess;
    @Autowired
    StoryFileUtil storyFileUtil;
    @Override
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("处理受托人同步响应数据：{}", GsonUtils.toJson(message));
        List<TrusteeMessage.Trustee> trusteeList = message.getData().getTrusteeList();
        List<Trustee> trustees = dbAccess.listTrustees();
        for(TrusteeMessage.Trustee trusteeMsg:trusteeList){
            for(Trustee trustee:trustees){
                if(trustee.getAddress().equals(new String(trusteeMsg.getAddress().toByteArray()))){
                    trustee.setState((int) trusteeMsg.getState());
                    dbAccess.putTrustee(trustee);
                }
            }
        }
        logger.info("受托人同步完成");
    }
}
