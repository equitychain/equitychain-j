package com.passport.web;

import afu.org.checkerframework.checker.oigj.qual.O;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
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
import com.passport.proto.*;
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
import java.nio.channels.Channel;
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
    public ResultDto newAccount(HttpServletRequest request) throws Exception {
        String password = request.getParameter("password");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(password);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        Account account = accountHandler.newAccount(password);
        if (account != null) {
            //当挖矿账户不存在时设置为挖矿账户
            accountHandler.setMinerAccountIfNotExists(account);
            provider.publishEvent(new SyncAccountEvent(account));
            return new ResultDto(ResultEnum.SUCCESS.getCode(), account);
        }
        return new ResultDto(ResultEnum.SYS_ERROR);

    }

    @GetMapping("/miner")
    public ResultDto miner(HttpServletRequest request) throws Exception {
        String minerAddress = request.getParameter("address");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(minerAddress);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY.getCode(),!SyncFlag.minerFlag);
        }
        if(SyncFlag.minerFlag){//启动时不更新受托人列表 需等下个周期在加入 只要有一个账户启动则所有启动
            List<Trustee> trustees = dbAccess.listTrustees();
            List<Trustee> localTrustees = new ArrayList<>();//添加可以出块账户
            if(channelsManager.getChannels().size() == 0){//服务器启动直接所有受托人启动
                SyncFlag.minerFlag = true;
                Set<String> address = storyFileUtil.getAddresses();
                //启动出块 需确认同步完成才能出块
                for(Trustee trustee:trustees){//更新受托人列表启动出块
                    for(String add:address){
                        if(trustee.getAddress().equals(add)){
                            SyncFlag.waitMiner.put(add,1);
                            localTrustees.add(trustee);
                            SyncFlag.minerFlag = false;
                            SyncFlag.keystoreAddressStatus.put(trustee.getAddress(),true);
                        }
                    }
                }
            }else{
                //启动出块 需确认同步完成才能出块
                for(Trustee trustee:trustees){//更新受托人列表启动出块
                    if(trustee.getAddress().equals(minerAddress)){
                        SyncFlag.waitMiner.put(minerAddress,1);
                        localTrustees.add(trustee);
                        SyncFlag.minerFlag = false;
                        SyncFlag.keystoreAddressStatus.put(trustee.getAddress(),true);
                    }
                }
            }
            provider.publishEvent(new GenerateBlockEvent(0L));
            //通知所有用户 本节点启动出块
            NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
            dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNT_MINER);
            for(Trustee s:localTrustees){
                TrusteeMessage.Trustee.Builder builder = TrusteeMessage.Trustee.newBuilder();
                builder.setAddress(ByteString.copyFrom(s.getAddress().getBytes()));
                builder.setVotes(s.getVotes());
                builder.setGenerateRate(s.getGenerateRate());
                builder.setStatus(s.getStatus());
                builder.setState(1);
                dataBuilder.addTrustee(builder.build());
            }
            NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
            builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
            builder.setData(dataBuilder.build());
            channelsManager.getChannels().writeAndFlush(builder.build());
        }
        return new ResultDto(ResultEnum.SUCCESS.getCode(),!SyncFlag.minerFlag);
    }


    @GetMapping("/generateGenesis")
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
    public ResultDto getAccountByAddress(@RequestParam("address") String address,@RequestParam("token") String token) {
        Map accountAccount = new HashMap();
        Optional<Account> blockOptional = dbAccess.getAccount(address+"_"+token);
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
                    logger.info("私钥解析异常："+e.getMessage());
                }
            }
            String[] addressToken = account.getAddress_token().split("_");
            accountAccount.put("balance", account.getBalance());
            accountAccount.put("address_token", account.getAddress_token());
            accountAccount.put("publicKey", publicKey);
            accountAccount.put("token", addressToken[1]);
            accountAccount.put("address", addressToken[0]);
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
                String[] addressToken = account.getAddress_token().split("_");
                Map accountMap = new HashMap();
                accountMap.put("address_token", account.getAddress_token());
                accountMap.put("balance", account.getBalance());
                accountMap.put("token",addressToken[1]);
                accountMap.put("address",addressToken[0]);
                accountMap.put("miner",SyncFlag.keystoreAddressStatus.get(addressToken[0]) == null ? false:SyncFlag.keystoreAddressStatus.get(addressToken[0]));//true 开启挖矿 false 未开启
                Trustee trustee = null;
                Optional<Trustee> trusteeOptional = dbAccess.getTrustee(addressToken[0]);
                if (trusteeOptional.isPresent()) {
                    trustee = trusteeOptional.get();
                }
                accountMap.put("isTrustee", trustee == null ? false : true);//是否为委托人
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
        Optional<Account> accountOptional = dbAccess.getAccount(address+"_"+Constant.MAIN_COIN);
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
            Set<String> set = storyFileUtil.getAddresses();
            for(String s:set){
                if(s.contains(address)){
                    walletFile = StoryFileUtil.getStoryFileUtil(new File(keystoreDir)).getAddressInfo(s);
                }
            }
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
//            Optional<Account> accountOptional = dbAccess.getAccount(walletFile.getAddress());
//            if (accountOptional.isPresent() || StringUtils.isNotBlank(accountOptional.get().getPrivateKey())) {
//                return new ResultDto(ResultEnum.WALLET_ACCOUNT_EXISTS);
//            }
            Set<String> set = storyFileUtil.getAddresses();
            for(String s:set){
                if(s.equals(walletFile.getAddress())){
                    return new ResultDto(ResultEnum.WALLET_ACCOUNT_EXISTS);
                }
            };
            ECKeyPair ecKeyPair = Wallet.decrypt(pwd, walletFile);
            address = ecKeyPair.getAddress();
            String fileName = WalletUtils.generateWalletFile(pwd, ecKeyPair, new File(keystoreDir), true);
            Account account = new Account(ecKeyPair.getAddress()+"_"+Constant.MAIN_COIN, ecKeyPair.exportPrivateKey(),
                    BigDecimal.ZERO,ecKeyPair.getAddress(),Constant.MAIN_COIN,"guest");
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

    /**
     * 测试使用查看网络连接情况
     * @return
     */
    @GetMapping("checkChannel")
    public ResultDto checkChannel() {
        List<Map> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        channelsManager.getChannels().forEach(v->{
            map1.put(v.id().asShortText(),v.remoteAddress().toString());
        });
        Map<String, Object> map2 = new HashMap<>();
        ChannelsManager.concurrentHashMap.forEach((k,v)->{
            map2.put(k,v);
        });
        list.add(map1);
        list.add(map2);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS.getCode(),list);//TODO:待实现
        return resultDto;
    }
}
