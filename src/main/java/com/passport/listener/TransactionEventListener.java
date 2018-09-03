package com.passport.listener;

import com.google.protobuf.ByteString;
import com.passport.core.Transaction;
import com.passport.event.SendTransactionEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.proto.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class TransactionEventListener {

  private static Logger logger = LoggerFactory.getLogger(TransactionEventListener.class);

  @Autowired
  private ChannelsManager channelsManager;

  @EventListener(SendTransactionEvent.class)
  public void SendTransaction(SendTransactionEvent event) {
    Transaction transaction = (Transaction) event.getSource();

    TransactionMessage.Transaction.Builder transactionBuilder = TransactionMessage.Transaction
        .newBuilder();
    transactionBuilder.setPayAddress(ByteString.copyFrom(transaction.getPayAddress()));
    transactionBuilder.setReceiptAddress(ByteString.copyFrom(transaction.getReceiptAddress()));
    transactionBuilder.setValue(ByteString.copyFrom(transaction.getValue()));
    transactionBuilder.setExtarData(ByteString.copyFrom(transaction.getExtarData()));
    transactionBuilder.setTimeStamp(ByteString.copyFrom(transaction.getTime()));
    transactionBuilder.setSignature(ByteString.copyFrom(transaction.getSignature()));
    transactionBuilder.setHash(ByteString.copyFrom(transaction.getHash()));
    transactionBuilder.setPublicKey(ByteString.copyFrom(transaction.getPublicKey()));

    NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
    dataBuilder.setDataType(DataTypeEnum.DataType.SEND_TRANSACTION);
    dataBuilder.setTransaction(transactionBuilder.build());

    NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
    builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
    builder.setData(dataBuilder);
    channelsManager.getChannels().writeAndFlush(builder.build());
  }
}
