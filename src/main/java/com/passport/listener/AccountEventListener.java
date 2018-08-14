package com.passport.listener;

import com.google.protobuf.ByteString;
import com.passport.core.Account;
import com.passport.event.SyncAccountEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听器处理新增账户事件
 */
@Component
public class AccountEventListener {
	private static Logger logger = LoggerFactory.getLogger(AccountEventListener.class);

	@Autowired
	private ChannelsManager channelsManager;

	/**
	 * 同步账户
	 * @param event
	 */
	@EventListener(SyncAccountEvent.class)
	public void syncAccount(SyncAccountEvent event) {
		Account account = (Account) event.getSource();

		//把新增账户广播到其它节点
		AccountMessage.Account.Builder accountBuilder = AccountMessage.Account.newBuilder();
		accountBuilder.setAddress(ByteString.copyFrom(account.getAddress().getBytes()));
		accountBuilder.setPrivateKey(ByteString.copyFrom(account.getPrivateKey().getBytes()));
		accountBuilder.setBalance(ByteString.copyFrom(String.valueOf(account.getBalance()).getBytes()));

		NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
		dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNT_SYNC);
		dataBuilder.setAccount(accountBuilder.build());

		NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
		builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
		builder.setData(dataBuilder.build());
		channelsManager.getChannels().writeAndFlush(builder.build());
	}
}
