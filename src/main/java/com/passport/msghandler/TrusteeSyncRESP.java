package com.passport.msghandler;

import com.google.protobuf.ByteString;
import com.passport.constant.SyncFlag;
import com.passport.core.Account;
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

import java.math.BigDecimal;
import java.util.ArrayList;
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
        int blockCycle = 0;
        List<Trustee> trustees = new ArrayList<>();
        for(TrusteeMessage.Trustee trusteeMsg:trusteeList){
            Trustee trustee = new Trustee();
            trustee.setIncome(new BigDecimal(0));
            trustee.setStatus((int) trusteeMsg.getStatus());
            trustee.setState((int) trusteeMsg.getState());
            trustee.setVotes(trusteeMsg.getVotes());
            trustee.setAddress(new String(trusteeMsg.getAddress().toByteArray()));
            trustee.setGenerateRate(trusteeMsg.getGenerateRate());
            dbAccess.putTrustee(trustee);//更新状态
            trustees.add(trustee);
            blockCycle = (int) trusteeMsg.getBlockCycle();
        }
        dbAccess.put(String.valueOf(blockCycle), trustees);
        SyncFlag.blockCycleList.put("blockCycle", trustees);//更新周期
        logger.info("受托人同步完成");
    }
}
