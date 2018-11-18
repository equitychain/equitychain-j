package com.passport.msghandler;

import com.google.protobuf.ByteString;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.*;
import com.passport.utils.BlockUtils;
import com.passport.utils.GsonUtils;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

@Component("DATA_REQ_TRUSTEE_SYNC")
public class TrusteeSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeSyncREQ.class);
    @Autowired
    BaseDBAccess dbAccess;
    @Autowired
    StoryFileUtil storyFileUtil;
    @Autowired
    BlockUtils blockUtils;
    @Override
    void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("处理受托人同步请求数据：{}", GsonUtils.toJson(message));
        //发送允许出块的列表
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.TRUSTEE_SYNC);
        List<Trustee> list = dbAccess.listTrustees();
        Long lastBlockHeight = Long.valueOf(dbAccess.getLastBlockHeight().get().toString());
        int blockCycle = blockUtils.getBlockCycle(lastBlockHeight+1l);
        for(Trustee trustee : list){
            if(trustee.getState() == 1){
                TrusteeMessage.Trustee.Builder builder2 = TrusteeMessage.Trustee.newBuilder();
                builder2.setAddress(ByteString.copyFrom(trustee.getAddress().getBytes()));
                builder2.setState(trustee.getState());
                builder2.setStatus(trustee.getStatus());
                builder2.setVotes(trustee.getVotes());
                builder2.setGenerateRate(trustee.getGenerateRate());
                builder2.setBlockCycle(blockCycle);
                dataBuilder.addTrustee(builder2);
            }
        }
        NettyMessage.Message.Builder builder1 = NettyMessage.Message.newBuilder();
        builder1.setData(dataBuilder.build());
        builder1.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
        ctx.writeAndFlush(builder1.build());
    }
}
