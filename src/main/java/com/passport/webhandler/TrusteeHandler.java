package com.passport.webhandler;

import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.annotations.RocksTransaction;
import com.passport.constant.SyncFlag;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.DBAccess;
import com.passport.peer.ChannelsManager;
import com.passport.proto.*;
import com.passport.utils.BlockUtils;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TrusteeHandler {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeHandler.class);

    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private ChannelsManager channelsManager;

    /**
     * 改变已经出块人的状态
     * @param trustee
     * @param blockCycle
     */
    public void changeStatus(Trustee trustee, int blockCycle) {
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        if(objectOptional.isPresent()){
            List<Trustee> list = (List<Trustee>)objectOptional.get();
            for(Trustee tee : list){
                if(tee.getAddress().equals(trustee.getAddress())){
                    tee.setStatus(0);//状态设置为已出场
                    break;
                }
            }
            dbAccess.put(String.valueOf(blockCycle), list);
        }
    }

    /**
     * 找出还未出块的受托人
     * @param blockCycle
     * @return
     */
    public List<Trustee> findValidTrustees(int blockCycle) {
        List<Trustee> trustees = new ArrayList<>();
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        if(objectOptional.isPresent()){
            List<Trustee> list = (List<Trustee>)objectOptional.get();
            for(Trustee tee : list){
                if(tee.getStatus() == 1){
                    trustees.add(tee);
                }
            }
        }
        return trustees;
    }

    public List<Trustee> getTrusteesBeforeTime(long newBlockHeight, int blockCycle) {
        logger.info("重新获取受托人列表");
        Long timestamp = blockUtils.getTimestamp4BlockCycle(newBlockHeight);
        //查询投票记录（status==1）,时间小于等于timestamp，按投票票数从高到低排列的101个受托人，放到101个受托人列表中
        List<Trustee> trustees = new ArrayList<>();
        try {
            List<Trustee> tru = dbAccess.listTrustees();
            SyncFlag.waitMiner.forEach((k, v) ->{
                for(Trustee trustee:tru){
                    if(trustee.getAddress().equals(k)){
                        trustee.setState(v);
                        SyncFlag.waitMiner.remove(k);
                        dbAccess.putTrustee(trustee);
                    }
                }
            });
            trustees = dbAccess.getTrusteeOfRangeBeforeTime(timestamp);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        dbAccess.put(String.valueOf(blockCycle), trustees);
        //没到一个周期发送一遍同步受托人列表请求 未启动出块不允许发送
        if(!SyncFlag.minerFlag){
            NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
            dataBuilder.setDataType(DataTypeEnum.DataType.TRUSTEE_SYNC);
            for(Trustee trustee : trustees){
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
            channelsManager.getChannels().writeAndFlush(builder1.build());
        }
        return trustees;
    }
}
