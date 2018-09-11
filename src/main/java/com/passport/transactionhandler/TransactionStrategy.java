package com.passport.transactionhandler;

import com.passport.core.Transaction;
import com.passport.crypto.eth.Sign;
import com.passport.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: xujianfeng
 * @create: 2018-09-10 17:39
 **/
@Component
public abstract class TransactionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(TransactionStrategy.class);

    public void handleTransaction(Transaction transaction){
        if(checkTransaction(transaction)){
            handle(transaction);
        }
    }

    /**
     * 交易数据验签
     * @param transaction
     * @return
     */
    private boolean checkTransaction(Transaction transaction){
        //验证签名
        Transaction trans = new Transaction();
        trans.setPayAddress(transaction.getPayAddress());
        trans.setReceiptAddress(transaction.getReceiptAddress());
        trans.setValue(transaction.getValue());
        trans.setExtarData(transaction.getExtarData());
        trans.setTime(transaction.getTime());
        //生成hash和生成签名sign使用的基础数据都应该一样 TODO 使用多语言开发时应使用同样的序列化算法
        String transactionJson = GsonUtils.toJson(trans);
        try {
            boolean flag = Sign.verify(transaction.getPublicKey(), new String(transaction.getSignature()), transactionJson);
            if (!flag) {
                return false;
            }
        } catch (Exception e) {
            logger.error("交易流水验签异常", e);
            return false;
        }
        return true;
    }

    protected abstract void handle(Transaction transaction);
}
