package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.crypto.ECDSAUtil;
import com.passport.crypto.eth.Credentials;
import com.passport.crypto.eth.Sign;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.event.SendTransactionEvent;
import com.passport.exception.CommonException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.transactionhandler.TransactionStrategy;
import com.passport.transactionhandler.TransactionStrategyContext;
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
    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    //todo 这是临时储存流水打包所消耗的egg，如果之后用多线程什么的这里需要进行更改储存方式
    private HashMap<byte[], BigDecimal> eggUsedTemp = new HashMap<>();
    @Autowired
    private TransactionStrategyContext transactionStrategyContext;

    /**
     * 发送交易，等待其它节点确认
     * @param payAddress
     * @param receiptAddress
     * @param value
     * @param extarData
     * @param tradeType 交易类型
     * @return
     */
    public Transaction sendTransaction(String payAddress, String receiptAddress, String value, String extarData, String password, String tradeType) throws CommonException {
        //判断是否解锁
        //if(LockUtil.isUnlock(payAddress)) {
        if(true) {
            //0.验证交易类型是否存在，交易类型对应的金额是否合法，是否已经注册过受托人或者投票人
            CommonException commonException = check4AllTradeType(tradeType, value, payAddress, receiptAddress);
            if (commonException != null){
                throw commonException;
            }

            //1.支付地址,接收地址是否合法 TODO 正则校验
            if (!WalletUtils.isValidAddress(payAddress) && !WalletUtils.isValidAddress(receiptAddress)) {
                throw new CommonException(ResultEnum.ADDRESS_ILLEGAL);
            }

            //2.支付地址、接收地址是否已经创建、支付方交易密码是否正确
            Optional<Account> accountPayOptional = dbAccess.getAccount(payAddress);
            if (!accountPayOptional.isPresent()) {
                throw new CommonException(ResultEnum.ACCOUNT_NOT_EXISTS);
            }
            Account accountPay = accountPayOptional.get();
            if (!password.equals(accountPay.getPassword())) {
                throw new CommonException(ResultEnum.PASSWORD_WRONG);
            }

            //3.余额是否足够支付（使用account的余额还是最后一条确认流水检查余额是否足够支付）TODO
            if(accountPay.getBalance().compareTo(CastUtils.castBigDecimal(value)) == -1){
                throw new CommonException(ResultEnum.BALANCE_NOTENOUGH);
            }

            //4.使用支付方的私钥加密数据 TODO 构造签名数据
            Transaction transaction = generateTransaction(payAddress, receiptAddress, value, extarData, accountPay);
            transaction.setTradeType(TransactionTypeEnum.statusOf(tradeType).getDesc().getBytes());

            //5.放到本地未确认流水中
            dbAccess.putUnconfirmTransaction(transaction);

            //6.发布广播交易事件
            provider.publishEvent(new SendTransactionEvent(transaction));
            return transaction;
        } else {
            throw new CommonException(ResultEnum.ACCOUNT_IS_LOCKED);
        }
    }

    /**
     * 检查特定交易类型的交易金额是否合法，防止余额不足的操作进入链中
     * @param tradeType
     * @param value
     * @return
     */
    public CommonException checkValue4AllTradeType(String tradeType, String value) {
        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.statusOf(tradeType);
        if(transactionTypeEnum == null){
            return new CommonException(ResultEnum.TRADETYPE_NOTFOUND);
        }
        if(TransactionTypeEnum.TRUSTEE_REGISTER.getDesc().equals(transactionTypeEnum.getDesc())){
            if(new BigDecimal(value).compareTo(Constant.FEE_4_REGISTER_TRUSTEE) != 0){
                return new CommonException(ResultEnum.TRADEAMOUNT_ILLEGAL);
            }
        }else if(TransactionTypeEnum.VOTER_REGISTER.getDesc().equals(transactionTypeEnum.getDesc())){
            if(new BigDecimal(value).compareTo(Constant.FEE_4_REGISTER_VOTER) != 0){
                return new CommonException(ResultEnum.TRADEAMOUNT_ILLEGAL);
            }
        }
        return null;
    }

    /**
     * 检查特定交易类型的交易金额是否合法
     * @param tradeType
     * @param value
     * @return
     */
    public CommonException check4AllTradeType(String tradeType, String value, String payAddress, String receiptAddress) {
        CommonException commonException = checkValue4AllTradeType(tradeType, value);
        if(commonException != null){
            return commonException;
        }

        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.statusOf(tradeType);
        if(TransactionTypeEnum.TRUSTEE_REGISTER.getDesc().equals(transactionTypeEnum.getDesc())){//已是委托人则不能重复注册
            Optional<Trustee> trusteeOptional = dbAccess.getTrustee(payAddress);
            if(trusteeOptional.isPresent() && trusteeOptional.get().getStatus() == 1){
                return new CommonException(ResultEnum.TRUSTEE_EXISTS);
            }
        }else if(TransactionTypeEnum.TRUSTEE_CANNEL.getDesc().equals(transactionTypeEnum.getDesc())){//只有受托人才能发起取消注册
            Optional<Trustee> trusteeOptional = dbAccess.getTrustee(payAddress);
            if(!trusteeOptional.isPresent() || trusteeOptional.get().getStatus() == 0){
                return new CommonException(ResultEnum.TRUSTEE_NOTEXISTS);
            }
        }else if(TransactionTypeEnum.VOTER_REGISTER.getDesc().equals(transactionTypeEnum.getDesc())){//已是投票人则不能重复注册
            Optional<Voter> voterOptional = dbAccess.getVoter(payAddress);
            if(voterOptional.isPresent() && voterOptional.get().getStatus() == 1){
                return new CommonException(ResultEnum.VOTER_EXISTS);
            }
        }else if(TransactionTypeEnum.VOTER_CANNEL.getDesc().equals(transactionTypeEnum.getDesc())){//只有投票人才能发起取消注册
            Optional<Voter> voterOptional = dbAccess.getVoter(payAddress);
            if(!voterOptional.isPresent() || voterOptional.get().getStatus() == 0){
                return new CommonException(ResultEnum.VOTER_NOTEXISTS);
            }
        }else if(TransactionTypeEnum.VOTE.getDesc().equals(transactionTypeEnum.getDesc())){//只能给受托人投票
            //给受托人投票，需判断受托人是否存在
            Optional<Trustee> trusteeOptional = dbAccess.getTrustee(receiptAddress);
            if(!trusteeOptional.isPresent() || trusteeOptional.get().getStatus() == 0){
                return new CommonException(ResultEnum.TRUSTEE_NOTEXISTS);
            }
        }

        return null;
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
        transaction.setReceiptAddress(receiptAddress == null?null:receiptAddress.getBytes());
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
     * 执行已确认流水
     * @param list
     */
    public void exec(List<Transaction> list) {
        //需要清空，不然会冗余很多
        eggUsedTemp.clear();
        for (Transaction transaction : list) {
            TransactionStrategy transactionStrategy = transactionStrategyContext.getTransactionStrategy(new String(transaction.getTradeType()));
            if(transactionStrategy != null){
                transactionStrategy.handleTransaction(transaction);
            }
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
            if(eggUsed.compareTo(BigDecimal.ZERO)> 0 && blockMaxEgg.compareTo(eggUsed)>=0 && transactions.size() < Constant.TRANS_SIZE){
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
        //取区块流水列表
        List<Transaction> transactions = blockLocal.getTransactions();
        List<byte[]> hashBytes = transactions.stream().map(Transaction::getHash).collect(Collectors.toList());

        //匹配区块流水和未确认流水
        List<Transaction> matchTransactions = new ArrayList<>();
        List<Transaction> unconfirmTransactions = dbAccess.listUnconfirmTransactions();
        for (Transaction untrans : unconfirmTransactions) {
            if(untrans.getPayAddress() == null){//挖矿流水直接成功
                matchTransactions.add(untrans);
                continue;
            }
            if(hashBytes.contains(untrans.getHash())){
                matchTransactions.add(untrans);
            }
        }

        //删除未确认流水
        deleteUnconfirmTransactions(matchTransactions);

        //匹配成功的流水放到已确认流水列表
        matchTransactions.forEach(transaction -> {
            dbAccess.putConfirmTransaction(transaction);
        });

        //执行流水
        exec(matchTransactions);
    }

    /**
     * 删除未确认流水
     * @param matchTransactions 与区块流水匹配成功的未确认流水
     */
    private void deleteUnconfirmTransactions(List<Transaction> matchTransactions) {
        matchTransactions.forEach(transaction -> {
            dbAccess.deleteUnconfirmTransaction(transaction.getHash().toString());
        });
    }
}
