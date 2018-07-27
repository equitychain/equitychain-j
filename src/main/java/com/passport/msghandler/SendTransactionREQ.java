package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.NettyMessage;
import com.passport.proto.TransactionMessage;
import com.passport.utils.GsonUtils;
import com.passport.utils.eth.ByteUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

/**
 * 服务端处理交易转账请求
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_REQ_SEND_TRANSACTION")//TODO 这里后期要优化为使用常量代替
public class SendTransactionREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(SendTransactionREQ.class);

    @Autowired
    private DBAccess dbAccess;

    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理交易转账请求数据：{}", GsonUtils.toJson(message));

        TransactionMessage.Transaction transaction = message.getData().getTransaction();
        Transaction trans = new Transaction();
        trans.setPayAddress(transaction.getPayAddress().toByteArray());
        trans.setReceiptAddress(transaction.getReceiptAddress().toByteArray());
        trans.setValue(transaction.getValue().toByteArray());
        trans.setExtarData(transaction.getExtarData().toByteArray());
        trans.setTime(ByteUtil.longToBytesNoLeadZeroes(transaction.getTimeStamp()));

        //使用公钥验签
        String transactionJson = GsonUtils.toJson(transaction);
        try{
            PublicKey publicKey = ECDSAUtil.getPublicKey(transaction.getPayAddress().toString());
            boolean flag = ECDSAUtil.verifyECDSASig(publicKey, transactionJson, transaction.getSignature().toByteArray());
            if(flag){
                //放到交易流水里面
                Optional<Transaction> transactionOptional = dbAccess.getTransaction(transaction.getHash().toString());
                if(!transactionOptional.isPresent()){
                    trans.setHash(transaction.getHash().toByteArray());
                }
            }
        }catch (Exception e){

        }
    }
}
