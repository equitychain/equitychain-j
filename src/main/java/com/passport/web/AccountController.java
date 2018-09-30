package com.passport.web;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.Account;
import com.passport.core.Trustee;
import com.passport.core.Voter;
import com.passport.crypto.eth.*;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.event.SyncAccountEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.transactionhandler.TransactionStrategyContext;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.AccountHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;

    @Value("${wallet.keystoreDir}")
    private String walletDir;


    @GetMapping("/test")
    public @ResponseBody
    Object test() throws Exception {
        accountHandler.test();
        return "sdfasdf";
    }

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

    @GetMapping("/setMinerAccount")
    public ResultDto setMinerAccount(HttpServletRequest request) throws Exception {
        String address = request.getParameter("address");
        if (address != null && !"".equalsIgnoreCase(address)) {
            Account account = accountHandler.setMinerAccount(address);
            if (account != null) {
                return new ResultDto(ResultEnum.SUCCESS.getCode(), account);
            }
        }
        return new ResultDto(ResultEnum.SYS_ERROR);
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
    public ResultDto getAccountByAddress(@RequestParam("address") String address) {
        Account account = new Account();
        Optional<Account> blockOptional = dbAccess.getAccount(address);
        if (blockOptional.isPresent()) {
            account = blockOptional.get();
            account.setPassword("");
            account.setPrivateKey("");
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(account);
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
     *
     * @param pwd
     * @param walletPath
     * @return
     */
    @PostMapping("backupWallet")
    public ResultDto backupWallet(@RequestParam("pwd") String pwd, @RequestParam("walletPath") String walletPath) {
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        WalletFile walletFile = null;
        try {
            Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(pwd, new File(walletPath));
            String mnem = bip39Wallet.getMnemonic();
            ECKeyPair ecKeyPair = bip39Wallet.getKeyPair();
            walletFile = Wallet.createLight(pwd, ecKeyPair);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.WALLET_BACKUP_EXCEP);
        }
        resultDto.setData(walletFile);
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
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        String address = "";
        try {
            Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(pwd, mnemonic);
            String fileName = WalletUtils.generateWalletFile(pwd, bip39Wallet.getKeyPair(), new File(keystoreDir), true);
            address = bip39Wallet.getKeyPair().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.WALLET_IMPORT_EXCEP);
        }
        resultDto.setData(address);
        return resultDto;
    }

    /**
     * 导入钱包（密码+秘钥文件）
     *
     * @param pwd
     * @param fileJson
     * @return
     */
    @PostMapping("importWalletByFile")
    public ResultDto importWalletByFile(@RequestParam("pwd") String pwd, @RequestParam("fileJson") String fileJson) {
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        String address = "";
        try {
            WalletFile walletFile = JSON.parseObject(fileJson, WalletFile.class);
            ECKeyPair ecKeyPair = Wallet.decrypt(pwd, walletFile);
            address = ecKeyPair.getAddress();
            String fileName = WalletUtils.generateWalletFile(pwd, ecKeyPair, new File(keystoreDir), true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.WALLET_IMPORT_EXCEP);
        }
        resultDto.setData(address);
        return resultDto;
    }
}
