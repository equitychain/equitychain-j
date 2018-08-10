package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.crypto.eth.Sign;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.NettyMessage;
import com.passport.proto.TransactionMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

/**
 * 服务端处理交易转账请求
 *
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
        trans.setTime(transaction.getTimeStamp().toByteArray());

        //使用公钥验签
        String transactionJson = GsonUtils.toJson(trans);
        try {
            PublicKey publicKey = Sign.publicKeyFromByte(transaction.getPublicKey().toByteArray());
            boolean flag = ECDSAUtil.verifyECDSASig(publicKey, transactionJson, transaction.getSignature().toByteArray());
            if (flag) {
                //放到交易流水里面
                Optional<Transaction> transactionOptional = dbAccess.getUnconfirmTransaction(transaction.getHash().toString());
                if (!transactionOptional.isPresent()) {
                    trans.setHash(transaction.getHash().toByteArray());
                    trans.setSignature(transaction.getSignature().toByteArray());
                    trans.setPublicKey(transaction.getPublicKey().toByteArray());
                    flag = dbAccess.putUnconfirmTransaction(trans);
                    logger.info("交易流水不存在，放到未确认流水中，结果：" + flag);
                    Optional<Transaction> tmp = dbAccess.getUnconfirmTransaction(transaction.getHash().toString());
                    if(tmp.isPresent()) {
                        logger.info(GsonUtils.toJson(tmp.get()));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("交易流水验签失败", e);
        }
    }
}
