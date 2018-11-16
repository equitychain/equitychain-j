package com.passport.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.passport.annotations.RocksTransaction;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Trustee;
import com.passport.core.Voter;
import com.passport.crypto.eth.*;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.event.GenerateBlockEvent;
import com.passport.event.SyncAccountEvent;
import com.passport.exception.CipherException;
import com.passport.listener.ApplicationContextProvider;
import com.passport.peer.ChannelsManager;
import com.passport.proto.DataTypeEnum;
import com.passport.proto.MessageTypeEnum;
import com.passport.proto.NettyData;
import com.passport.proto.NettyMessage;
import com.passport.transactionhandler.TransactionStrategyContext;
import com.passport.utils.CheckUtils;
import com.passport.utils.HttpUtils;
import com.passport.utils.SerializeUtils;
import com.passport.utils.StoryFileUtil;
import com.passport.webhandler.AccountHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 账户
 * 处理区块web接口请求
 *
 * @author: xujianfeng
 * @create: 2018-07-23 15:41
 **/
@RestController
@RequestMapping("/account")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Value("${wallet.keystoreDir}")
    public String keystoreDir;

    @Autowired
    AccountHandler accountHandler;
//    @Autowired
//    DBAccess dbAccess;
//    @Value("${wallet.keystoreDir}")
//    private String walletDir;

    @Autowired
    TransactionStrategyContext transactionStrategyContext;
    @Autowired
    BaseDBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    @Autowired
    private ChannelsManager channelsManager;
    @Autowired
    private StoryFileUtil storyFileUtil;

    @Value("${wallet.keystoreDir}")
    private String walletDir;

    @GetMapping("/new")
    @RocksTransaction
    public ResultDto newAccount(HttpServletRequest request) throws Exception {
        String password = request.getParameter("password");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(password);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        String clientIP = HttpUtils.getLocalHostLANAddress().getHostAddress();
        Account account = accountHandler.newAccount(password);
        if (account != null) {
            //当挖矿账户不存在时设置为挖矿账户
            accountHandler.setMinerAccountIfNotExists(account);
            dbAccess.rocksDB.put( (clientIP+"_"+new String(account.getAddress().getBytes())).getBytes(), SerializeUtils.serialize(new String(account.getAddress().getBytes())));
            dbAccess.rocksDB.put( (new String(account.getAddress().getBytes())+"_"+clientIP).getBytes(),SerializeUtils.serialize(new String(account.getAddress().getBytes())));
            provider.publishEvent(new SyncAccountEvent(account));
            return new ResultDto(ResultEnum.SUCCESS.getCode(), account);
        }
        return new ResultDto(ResultEnum.SYS_ERROR);

    }

    @GetMapping("/miner")
    @RocksTransaction
    public ResultDto miner(HttpServletRequest request) throws Exception {
        Set<String> address = storyFileUtil.getAddresses();
        List<Trustee> trustees = dbAccess.listTrustees();
        for(Trustee trustee:trustees){//更新受托人列表启动出块
            for(String add:address){
                    if(trustee.getAddress().equals(add)){
                        if(trustee.getState() == 1){
                            return new ResultDto(ResultEnum.SUCCESS);
                        }
                        trustee.setState(1);
                        dbAccess.putTrustee(trustee);
                    }
            }
        }
        //启动出块
        provider.publishEvent(new GenerateBlockEvent(0L));
        //通知所有用户 本节点启动出块
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNT_MINER);

        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
        builder.setData(dataBuilder.build());
        channelsManager.getChannels().writeAndFlush(builder.build());
        return new ResultDto(ResultEnum.SUCCESS);
    }

    @GetMapping("/generateGenesis")
    @RocksTransaction
    public ResultDto generateGenesis(HttpServletRequest request) throws Exception {
        accountHandler.generateTrustees();
        return new ResultDto(ResultEnum.SUCCESS);
    }

    //解锁账号
    /*@GetMapping("/unlock")
    public ResultDto unlock(HttpServletRequest request) throws Exception {
        String address = request.getParameter("address");
        String time = request.getParameter("time");
        String password = request.getParameter("password");
        if(CheckUtils.checkParamIfEmpty(address,password)){
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        fileUtil.flush();
        boolean lock;
        if(CheckUtils.checkParamIfEmpty(time)){
            lock = LockUtil.unLockAddr(address,password,fileUtil);
        }else{
            lock = LockUtil.unLockAddr(address,password,fileUtil,Long.parseLong(time));
        }
        return new ResultDto(ResultEnum.SUCCESS.getCode(), lock);
    }*/

    /**
     * 根据地址获取账户信息
     *
     * @param address ：地址
     * @return
     */
    @GetMapping("getAccountByAddress")
    public ResultDto getAccountByAddress(@RequestParam("address") String address) {
        Map accountAccount = new HashMap();
        Optional<Account> blockOptional = dbAccess.getAccount(address);
        if (blockOptional.isPresent()) {
            Account account = blockOptional.get();
            String publicKey = "";
            if (!StringUtils.isEmpty(account.getPrivateKey())) {
                ECKeyPair ecKeyPair = null;
                try {
                    ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix(account.getPrivateKey()));
                    publicKey = Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKeyValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("私钥解析异常："+e.getMessage());
                }
            }
            accountAccount.put("balance", account.getBalance());
            accountAccount.put("address", account.getAddress());
            accountAccount.put("publicKey", publicKey);
        }else {
            return new ResultDto(ResultEnum.ACCOUNT_NOT_EXISTS);
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(accountAccount);
        return resultDto;
    }

    /**
     * 账户列表
     *
     * @return
     */
    @GetMapping("accountList")
    public ResultDto accountList() {
        Map resultMap = new HashMap();
        List<Map> accounts = new ArrayList<>();
        List<Account> accountList = dbAccess.listAccounts();
        BigDecimal sumBalance = BigDecimal.ZERO;
        for (Account account : accountList) {
            if (!StringUtils.isEmpty(account.getPrivateKey())) {
                Map accountMap = new HashMap();
                accountMap.put("address", account.getAddress());
                accountMap.put("balance", account.getBalance());
                accounts.add(accountMap);
                sumBalance = sumBalance.add(account.getBalance());
            }
        }
        resultMap.put("accounts", accounts);
        resultMap.put("sumBalance", sumBalance);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(resultMap);
        return resultDto;
    }

    /**
     * 获取账户身份
     *
     * @param address
     * @return
     */
    @GetMapping("getAccountIdentity")
    public ResultDto getAccountIdentity(@RequestParam("address") String address) {
        ResultDto resultDto = new ResultDto();
        Map resultMap = new HashMap();
        Account account = null;
        Optional<Account> accountOptional = dbAccess.getAccount(address);
        if (accountOptional.isPresent()) {
            account = accountOptional.get();
        }
        Trustee trustee = null;
        Optional<Trustee> trusteeOptional = dbAccess.getTrustee(address);
        if (trusteeOptional.isPresent()) {
            trustee = trusteeOptional.get();
        }
        Voter voter = null;
        Optional<Voter> voterOptional = dbAccess.getVoter(address);
        if (voterOptional.isPresent()) {
            voter = voterOptional.get();
        }
        resultMap.put("address", address);
        resultMap.put("balance", account == null ? BigDecimal.ZERO : account.getBalance());
        resultMap.put("isTrustee", trustee == null ? false : true);//是否为委托人
        resultMap.put("trusteeDeposit", Constant.FEE_4_REGISTER_TRUSTEE);//委托人押金
        resultMap.put("isVoter", voter == null ? false : true);//是否为投票人
        resultMap.put("voterDeposit", voter == null ? BigDecimal.ZERO : voter.getVoteNum());//投票人剩余票数
        resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(resultMap);
        return resultDto;
    }

    /**
     * 备份钱包
     * @param pwd
     * @param address
     * @param targePath
     * @return
     */
    @PostMapping("backupWallet")
    public ResultDto backupWallet(@RequestParam("pwd") String pwd, @RequestParam("address") String address, @RequestParam("targePath") String targePath) {
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        WalletFile walletFile = null;
        String fileName = "";
        try {
            walletFile = StoryFileUtil.getStoryFileUtil(new File(keystoreDir)).getAddressInfo(address);
            ECKeyPair ecKeyPair = Wallet.decrypt(pwd, walletFile);
            fileName = WalletUtils.generateWalletFile(pwd, ecKeyPair, new File(targePath), true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.WALLET_BACKUP_EXCEP);
        }
        resultDto.setData(fileName);
        return resultDto;
    }

    /**
     * 导入钱包（密码+助记词）
     *
     * @param pwd
     * @param mnemonic
     * @return
     */
    @PostMapping("importWallet")
    public ResultDto importWallet(@RequestParam("pwd") String pwd, @RequestParam("mnemonic") String mnemonic) {
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);//TODO:待实现
        return resultDto;
    }


    /**
     * 导入钱包（密码+秘钥文件）
     *
     * @param pwd
     * @param walletPath
     * @return
     */
    @PostMapping("importWalletByFile")
    public ResultDto importWalletByFile(@RequestParam("pwd") String pwd, @RequestParam("walletPath") String walletPath) {
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        String address = "";
        try {
            File file = new File(walletPath);
            WalletFile walletFile = new ObjectMapper().readValue(file, WalletFile.class);
            Optional<Account> accountOptional = dbAccess.getAccount(Numeric.HEX_PREFIX + walletFile.getAddress());
            if (accountOptional.isPresent()) {
                return new ResultDto(ResultEnum.WALLET_ACCOUNT_EXISTS);
            }
            ECKeyPair ecKeyPair = Wallet.decrypt(pwd, walletFile);
            address = ecKeyPair.getAddress();
            String fileName = WalletUtils.generateWalletFile(pwd, ecKeyPair, new File(keystoreDir), true);
            Account account = new Account(ecKeyPair.getAddress(), ecKeyPair.exportPrivateKey(), BigDecimal.ZERO);
            boolean res = dbAccess.putAccount(account);
            if (!res) {
                File walletfile = new File(walletPath + File.separator + fileName);
                if (walletfile.exists()) {
                    walletfile.delete();
                }
                return new ResultDto(ResultEnum.WALLET_IMPORT_EXCEP);
            }
        } catch (CipherException e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.WALLET_PWD_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.WALLET_IMPORT_EXCEP);
        }
        resultDto.setData(address);
        return resultDto;
    }

    @GetMapping("getTest")
    public @ResponseBody Object getTest(String clientIP){
        List<String> list = dbAccess.seekByKey(clientIP);
        Map<String,String> map = new HashMap<>();
        for(String s:list){
            map.put(clientIP+"_"+s,s);
        }
        return map;
    }
}
