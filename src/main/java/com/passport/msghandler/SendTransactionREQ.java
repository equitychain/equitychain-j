package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.exception.CommonException;
import com.passport.proto.NettyMessage;
import com.passport.proto.TransactionMessage;
import com.passport.utils.CheckUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.TransactionHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @Autowired
    private TransactionHandler transactionHandler;

    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理交易转账请求数据：{}", GsonUtils.toJson(message));
//        if(!SyncFlag.isNextBlockSyncFlag()){
//            logger.info("正在主动同步区块，暂时不处理流水广播消息");
//            return;
//        }

        TransactionMessage.Transaction transaction = message.getData().getTransaction();
        Transaction trans = new Transaction();
        trans.setPayAddress(transaction.getPayAddress().toByteArray());
        trans.setReceiptAddress(transaction.getReceiptAddress().toByteArray());
        trans.setValue(transaction.getValue().toByteArray());
        trans.setExtarData(transaction.getExtarData().toByteArray());
        trans.setTime(transaction.getTimeStamp().toByteArray());
        trans.setToken(transaction.getToken().toByteArray());
        //使用公钥验签
        //String transactionJson = GsonUtils.toJson(trans);
        try {
            //PublicKey publicKey = Sign.publicKeyFromByte(transaction.getPublicKey().toByteArray());
            //boolean flag = ECDSAUtil.verifyECDSASig(publicKey, transactionJson, transaction.getSignature().toByteArray());
            boolean flag = CheckUtils.checkTransaction(trans);
            if (flag) {
                //放到未确认交易流水里面
                Optional<Transaction> transactionOptional = dbAccess.getUnconfirmTransaction(new String(transaction.getHash().toByteArray()));
                if (!transactionOptional.isPresent()) {
                    trans.setSignature(transaction.getSignature().toByteArray());
                    trans.setPublicKey(transaction.getPublicKey().toByteArray());
                    trans.setStatus(Integer.parseInt(new String(transaction.getStatus().toByteArray())));
                    trans.setNonce(Integer.parseInt(new String(transaction.getNonce().toByteArray())));
                    trans.setEggUsed(transaction.getEggUsed().toByteArray());
                    trans.setEggMax(transaction.getEggMax().toByteArray());
                    trans.setEggPrice(transaction.getEggPrice().toByteArray());
                    trans.setTradeType(transaction.getTradeType().toByteArray());
                    trans.setHash(transaction.getHash().toByteArray());
                    trans.setBlockHeight(trans.getBlockHeight());

                    //特殊流水校验
                    CommonException commonException = transactionHandler.check4AllTradeType(
                            new String(trans.getTradeType()),
                            new String(trans.getValue()),
                            new String(trans.getPayAddress()),
                            new String(trans.getReceiptAddress()));
                    if(commonException != null){
                        return;
                    }

                    flag = dbAccess.putUnconfirmTransaction(trans);
                    logger.info("交易流水不存在，放到未确认流水中，结果：" + flag);
                    Optional<Transaction> tmp = dbAccess.getUnconfirmTransaction(new String(transaction.getHash().toByteArray()));
                    if(tmp.isPresent()) {
                        logger.info(GsonUtils.toJson(tmp.get()));
                    }
                }
            }
        } catch (Exception e) {
            logger.info("交易流水验签失败", e);
        }
    }
}
