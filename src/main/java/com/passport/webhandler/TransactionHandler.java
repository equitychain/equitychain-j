package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.db.dbhelper.DBAccess;
import com.passport.enums.ResultEnum;
import com.passport.event.SendTransactionEvent;
import com.passport.exception.CommonException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.GsonUtils;
import com.passport.utils.eth.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;

/**
 * 处理
 * @author: xujianfeng
 * @create: 2018-07-26 17:17
 **/
@Component
public class TransactionHandler {
    private static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;

    /**
     * 发送交易，等待其它节点确认
     * @param payAddress
     * @param receiptAddress
     * @param value
     * @param extarData
     * @return
     */
    public Transaction sendTransaction(String payAddress, String receiptAddress, String value, String extarData) throws CommonException {
        //1.支付地址,接收地址是否合法 TODO 正则校验

        //2.支付地址、接收地址是否已经创建
        Optional<Account> accountPayOptional = dbAccess.getAccount(payAddress);
        Optional<Account> accountReceiptOptional = dbAccess.getAccount(payAddress);
        if(!accountPayOptional.isPresent()){
            throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
        }
        if(!accountReceiptOptional.isPresent()){
            throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
        }

        //3.支付地址不在本节点创建，没有私钥文件，不能执行转账 TODO 修改私钥的保存形式

        //4.余额是否足够支付（使用account的余额还是最后一条确认流水检查余额是否足够支付）TODO

        //5.使用支付方的私钥加密数据
        Transaction transaction = new Transaction();
        transaction.setPayAddress(payAddress.getBytes());
        transaction.setReceiptAddress(receiptAddress.getBytes());
        transaction.setValue(value.getBytes());
        transaction.setExtarData(extarData.getBytes());
        transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
        //生成hash和生成签名sign使用的基础数据都应该一样
        String transactionJson = GsonUtils.toJson(transaction);
        try{
            //使用私钥签名数据
            Account accountPay = accountPayOptional.get();
            PrivateKey privateKey = ECDSAUtil.getPrivateKey(accountPay.getPrivateKey());
            transaction.setSignature(ECDSAUtil.applyECDSASig(privateKey, transactionJson));
        }catch (Exception e){
            logger.error("处理私钥信息异常", e);
            throw new CommonException(ResultEnum.SYS_ERROR);
        }

        //6.计算交易hash
        transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());

        //7.发布广播交易事件
        provider.publishEvent(new SendTransactionEvent(transaction));

        return transaction;
    }
}
