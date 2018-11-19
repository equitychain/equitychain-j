//package com.passport.listener;
//
//import com.google.protobuf.ByteString;
//import com.passport.core.Account;
//import com.passport.event.SyncAccountEvent;
//import com.passport.peer.ChannelsManager;
//import com.passport.proto.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
///**
// * 监听器处理新增账户事件
// */
//@Component
//public class AccountIPEventListener {
//	private static Logger logger = LoggerFactory.getLogger(AccountIPEventListener.class);
//
//	@Autowired
//	private ChannelsManager channelsManager;
//
//	/**
//	 * 同步账户
//	 * @param event
//	 */
//	@EventListener(SyncAccountEvent.class)
//	public void syncAccount(SyncAccountEvent event) {
//		NettyData.Data.Builder dataBuilder1 = NettyData.Data.newBuilder();
//		dataBuilder1.setDataType(DataTypeEnum.DataType.ACCOUNTIP_SYNC);
//		for (String address : localAddress) {
//			AccountMessage.Account.Builder builder1 = AccountMessage.Account.newBuilder();
//			builder1.setAddress(ByteString.copyFrom(address.getBytes()));
//			dataBuilder1.addAccounts(builder1.build());
//		}
//		NettyMessage.Message.Builder builder1 = NettyMessage.Message.newBuilder();
//		builder1.setData(dataBuilder1.build());
//		builder1.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
//		channelsManager.getChannels().writeAndFlush(builder1.build());
//		//发送已经启动的受托人列表状态
//		NettyData.Data.Builder dataBuilder2 = NettyData.Data.newBuilder();
//		dataBuilder2.setDataType(DataTypeEnum.DataType.TRUSTEE_SYNC);
//		NettyMessage.Message.Builder builder2 = NettyMessage.Message.newBuilder();
//		builder2.setData(dataBuilder2.build());
//		builder2.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
//		channelsManager.getChannels().writeAndFlush(builder2.build());
//	}
//}
