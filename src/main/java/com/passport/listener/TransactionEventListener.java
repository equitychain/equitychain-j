package com.passport.listener;

import com.google.protobuf.ByteString;
import com.passport.core.Transaction;
import com.passport.event.SendTransactionEvent;
import com.passport.peer.ChannelsManager;
import com.passport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * 监听器处理发送交易事件
 */
@Component
public class TransactionEventListener {
    private static Logger logger = LoggerFactory.getLogger(TransactionEventListener.class);

    @Autowired
    private ChannelsManager channelsManager;

    /**
     * 广播交易记录到其它节点
     *
     * @param event
     */
    @EventListener(SendTransactionEvent.class)
    public void SendTransaction(SendTransactionEvent event) {
        Transaction transaction = (Transaction) event.getSource();

        //构造请求消息
        TransactionMessage.Transaction.Builder transactionBuilder = TransactionMessage.Transaction.newBuilder();
        transactionBuilder.setPayAddress(ByteString.copyFrom(transaction.getPayAddress() == null ? "".getBytes() : transaction.getPayAddress()));
        transactionBuilder.setReceiptAddress(ByteString.copyFrom(transaction.getReceiptAddress() == null ? "".getBytes() : transaction.getReceiptAddress()));
        transactionBuilder.setValue(ByteString.copyFrom(transaction.getValue() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getValue()));
        transactionBuilder.setExtarData(ByteString.copyFrom(transaction.getExtarData() == null ? "".getBytes() : transaction.getExtarData()));
        transactionBuilder.setTimeStamp(ByteString.copyFrom(transaction.getTime() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getTime()));
        transactionBuilder.setSignature(ByteString.copyFrom(transaction.getSignature() == null ? "".getBytes() : transaction.getSignature()));
        transactionBuilder.setHash(ByteString.copyFrom(transaction.getHash() == null ? "".getBytes() : transaction.getHash()));
        transactionBuilder.setPublicKey(ByteString.copyFrom(transaction.getPublicKey() == null ? "".getBytes() : transaction.getPublicKey()));
        transactionBuilder.setStatus(ByteString.copyFrom(transaction.getStatus() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getStatus().toString().getBytes()));
        transactionBuilder.setNonce(ByteString.copyFrom(transaction.getNonce() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getNonce().toString().getBytes()));
        transactionBuilder.setEggUsed(ByteString.copyFrom(transaction.getEggUsed() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getEggUsed()));
        transactionBuilder.setEggMax(ByteString.copyFrom(transaction.getEggMax() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getEggMax()));
        transactionBuilder.setEggPrice(ByteString.copyFrom(transaction.getEggPrice() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getEggPrice()));
        transactionBuilder.setBlockHeight(ByteString.copyFrom(transaction.getBlockHeight() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getBlockHeight()));
        transactionBuilder.setTradeType(ByteString.copyFrom(transaction.getTradeType() == null ? BigInteger.ZERO.toString().getBytes() : transaction.getTradeType()));
        transactionBuilder.setToken(ByteString.copyFrom(transaction.getToken() == null ? "".getBytes() : transaction.getToken()));
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.SEND_TRANSACTION);
        dataBuilder.setTransaction(transactionBuilder.build());

        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_REQ);
        builder.setData(dataBuilder);
        channelsManager.getChannels().writeAndFlush(builder.build());
    }
}
