package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.crypto.ECDSAUtil;
import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.Sign;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.enums.ResultEnum;
import com.passport.event.SendTransactionEvent;
import com.passport.exception.CommonException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.utils.eth.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    public Transaction sendTransaction(String payAddress, String receiptAddress, String value, String extarData, String password) throws CommonException {
        //判断是否解锁
        //if(LockUtil.isUnlock(payAddress)) {
        if(true) {
            //1.支付地址,接收地址是否合法 TODO 正则校验
            if (!WalletUtils.isValidAddress(payAddress) && !WalletUtils.isValidAddress(receiptAddress)) {
                throw new CommonException(ResultEnum.ADDRESS_ILLEGAL);
            }

            //2.支付地址、接收地址是否已经创建、支付方交易密码是否正确
            Optional<Account> accountPayOptional = dbAccess.getAccount(payAddress);
            Optional<Account> accountReceiptOptional = dbAccess.getAccount(receiptAddress);
            if(!accountPayOptional.isPresent()){
                throw new CommonException(ResultEnum.PASSWORD_WRONG);
            }
            Account accountPay = accountPayOptional.get();
            if(!password.equals(accountPay.getPassword())){
                throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
            }

            /*if(!accountReceiptOptional.isPresent()){
                throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
            }*/

            //3.支付地址不在本节点创建，没有私钥文件，不能执行转账 TODO 修改私钥的保存形式

            //4.余额是否足够支付（使用account的余额还是最后一条确认流水检查余额是否足够支付）TODO
            /*if(accountPay.getBalance().compareTo(CastUtils.castBigDecimal(value)) == -1){
                throw new CommonException(ResultEnum.BALANCE_NOTENOUGH);
            }*/

            //5.使用支付方的私钥加密数据 TODO 构造签名数据
            Transaction transaction = generateTransaction(payAddress, receiptAddress, value, extarData, accountPay);

            //6.发布广播交易事件
            provider.publishEvent(new SendTransactionEvent(transaction));
            return transaction;
        }else{
            throw new CommonException(ResultEnum.ACCOUNT_IS_LOCKED);
        }
    }

    /**
     * 构造交易流水
     * @param payAddress
     * @param receiptAddress
     * @param value
     * @param extarData
     * @param accountPay
     * @return
     */
    public Transaction generateTransaction(String payAddress, String receiptAddress, String value, String extarData, Account accountPay) {
        Transaction transaction = new Transaction();
        transaction.setPayAddress(payAddress.getBytes());
        transaction.setReceiptAddress(receiptAddress.getBytes());
        transaction.setValue(value.getBytes());
        transaction.setExtarData(extarData.getBytes());
        transaction.setTime(ByteUtil.longToBytesNoLeadZeroes(System.currentTimeMillis()));
        //生成hash和生成签名sign使用的基础数据都应该一样
        String transactionJson = GsonUtils.toJson(transaction);
        try {
            //使用私钥签名数据
            PrivateKey privateKey = Sign.privateKeyFromString(accountPay.getPrivateKey());
            transaction.setSignature(ECDSAUtil.applyECDSASig(privateKey, transactionJson));
            //设置交易公钥
            Credentials credentials = Credentials.create(accountPay.getPrivateKey());
            transaction.setPublicKey(credentials.getEcKeyPair().getPublicKey().getEncoded());
        } catch (Exception e) {
            logger.error("处理私钥信息异常", e);
            throw new CommonException(ResultEnum.SYS_ERROR);
        }
        //计算交易hash
        transaction.setHash(ECDSAUtil.applySha256(transactionJson).getBytes());

        return transaction;
    }

    /**
     * 执行流水,
     * @param currentBlock
     */
    public void exec(Block currentBlock) {
        for (Transaction transaction : currentBlock.getTransactions()) {
            String receiptAddress = new String(transaction.getReceiptAddress());//收款地址
            byte[] payAddressByte = transaction.getPayAddress();//付款地址byte
            BigDecimal valueBigDecimal = CastUtils.castBigDecimal(new String(transaction.getValue()));//交易金额

            //收款账户
            Optional<Account> receiptOptional = dbAccess.getAccount(receiptAddress);
            if (!receiptOptional.isPresent()) {//可能暂时没有同步过来，先构造一个Account对象
                receiptOptional = Optional.of(new Account(receiptAddress, BigDecimal.ZERO));
            }
            Account accountReceipt = receiptOptional.get();

            //无付款人则是挖矿奖励
            if (payAddressByte == null) {
                accountReceipt.setBalance(accountReceipt.getBalance().add(valueBigDecimal));
                dbAccess.putAccount(accountReceipt);
                continue;
            }
            String payAddress = new String(payAddressByte);//付款地址

            //验证签名
            //TODO 构造签名数据
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
                    logger.info("交易验签不通过");
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //账户转账
            Optional<Account> payOptional = dbAccess.getAccount(payAddress);
            Account accountPay = payOptional.get();
            //验证账户余额
            if (accountPay.getBalance().compareTo(valueBigDecimal) == -1) {
                logger.info("余额不足");
                continue;
            }

            accountPay.setBalance(accountPay.getBalance().subtract(valueBigDecimal));
            accountReceipt.setBalance(accountReceipt.getBalance().add(valueBigDecimal));
            dbAccess.putAccount(accountPay);
            dbAccess.putAccount(accountReceipt);
        }
    }
}
