package com.passport.listener;

import com.google.protobuf.ByteString;
import com.passport.core.Account;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.event.SyncAccountEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.*;
import com.passport.utils.BlockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 监听器处理新增账户事件
 */
@Component
public class TrusteeEventListener {
	private static Logger logger = LoggerFactory.getLogger(TrusteeEventListener.class);

	@Autowired
	private ChannelsManager channelsManager;
	@Autowired
	BlockUtils blockUtils;
	@Autowired
	BaseDBAccess dbAccess;

	/**
	 * 同步账户
	 * @param event
	 */
	@EventListener(SyncAccountEvent.class)
	public void syncAccount(SyncAccountEvent event) {
		Long lastBlockHeight = Long.valueOf(dbAccess.getLastBlockHeight().get().toString());
		int blockCycle = blockUtils.getBlockCycle(lastBlockHeight+1l);
		List<Trustee> trustees = (List<Trustee>) event.getSource();
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
}
