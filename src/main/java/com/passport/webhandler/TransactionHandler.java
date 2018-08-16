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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 处理
 * @author: xujianfeng
 * @create: 2018-07-26 17:17
 **/
@Component
public class TransactionHandler {
    private static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);
    private static final int transSize = 100;
    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    //todo 这是临时储存流水打包所消耗的egg，如果之后用多线程什么的这里需要进行更改储存方式
    private HashMap<byte[], BigDecimal> eggUsedTemp = new HashMap<>();

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
            if (!accountPayOptional.isPresent()) {
                throw new CommonException(ResultEnum.PASSWORD_WRONG);
            }
            Account accountPay = accountPayOptional.get();
            if (!password.equals(accountPay.getPassword())) {
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
        } else {
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
        //需要清空，不然会冗余很多
        eggUsedTemp.clear();
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

    //获取需要打包的流水
    public List<Transaction> getBlockTrans(List<Transaction> unconfirmTrans, BigDecimal blockMaxEgg) {
        List<Transaction> transactions = new ArrayList<>();
        //排序 gasPrice大的排前面
        //todo 这里是根据利益最大化进行一个流水的筛选，要进行修改，这里我只根据eggPrice排
        Collections.sort(unconfirmTrans, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                BigDecimal price1 = o1 == null || o1.getEggPrice() == null ? BigDecimal.ZERO : new BigDecimal(new String(o1.getEggPrice()));
                BigDecimal price2 = o2 == null || o2.getEggPrice() == null ? BigDecimal.ZERO : new BigDecimal(new String(o2.getEggPrice()));
                return price2.compareTo(price1);
            }
        });
        for (Transaction tran : unconfirmTrans) {
            //流水的gas消耗
            BigDecimal eggUsed = getEggUsedByTrans(tran);
            //只要流水的egg和区块的egg足够就能够进行打包
            if(eggUsed.compareTo(BigDecimal.ZERO)> 0 && blockMaxEgg.compareTo(eggUsed)>=0 && transactions.size() < transSize){
                System.out.println("======add======="+eggUsed);
                transactions.add(tran);
                eggUsedTemp.put(tran.getHash(),eggUsed);
                blockMaxEgg = blockMaxEgg.subtract(eggUsed);
                if(blockMaxEgg.compareTo(BigDecimal.ZERO) == 0){
                    break;
                }
            }
        }
        return transactions;
    }

    //打包流水消耗的egg
    public BigDecimal getEggUsedByTrans(Transaction transaction) {
        //TODO 这里的egg要那些参数计算，怎么计算
        //计算损耗egg，更新流水的eggUsed  注意，要确保流水的limit要大于或等于used
        long begin = System.currentTimeMillis();
        try {
            Thread.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        BigDecimal eggUsed = new BigDecimal(transaction.getEggUsed() == null ? "0" : new String(transaction.getEggUsed()));
        BigDecimal eggMax = new BigDecimal(transaction.getEggMax() == null ? "0" : new String(transaction.getEggMax()));
        BigDecimal curUse = new BigDecimal(end - begin);
        if (eggMax.compareTo(eggUsed.add(curUse)) >= 0) {
            //消耗燃料
            //todo 有个问题，就是未确认流水的已使用egg怎么让其他节点同步
            transaction.setEggUsed(eggUsed.add(curUse).toString().getBytes());
            System.out.println("======test=======");
            return curUse;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTempEggByHash(byte[] transHash) {
        return eggUsedTemp.get(transHash);
    }

    /**
     * 已验证的区块中的流水和本地未确认流水进行匹配，如果本地未确认流水在区块中，则删除未确认流水
     *
     * @param blockLocal
     */
    public void matchUnConfirmTransactions(Block blockLocal) {
        List<Transaction> transactions = blockLocal.getTransactions();
        List<byte[]> hashBytes = transactions.stream().map(Transaction::getHash).collect(Collectors.toList());

        List<Transaction> unconfirmTransactions = dbAccess.listUnconfirmTransactions();
        unconfirmTransactions.forEach(untrans -> {
            if(hashBytes.contains(untrans.getHash())){
                //删除未确认流水，
            }
        });
    }
}
