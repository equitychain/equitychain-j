package com.passport.web;

import com.passport.core.Account;
import com.passport.crypto.eth.ECKeyPair;
import com.passport.crypto.eth.WalletUtils;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.webhandler.AccountHandler;
import com.passport.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;

/**
 * 账户
 * 处理区块web接口请求
 * @author: xujianfeng
 * @create: 2018-07-23 15:41
 **/
@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountHandler accountHandler;
//    @Autowired
//    DBAccess dbAccess;
//    @Value("${wallet.keystoreDir}")
//    private String walletDir;

    @GetMapping("/new")
    public ResultDto newAccount(HttpServletRequest request) throws Exception {

        String password = request.getParameter("password");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(password);
        if(flag){
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        //        File file = new File(walletDir);
//        if (!file.exists()) {
//            file.mkdir();
//        }
//
//        //创建公私钥并生成keystore文件
//        ECKeyPair keyPair = WalletUtils.generateNewWalletFile(password, new File(walletDir), true);
//        Account account = new Account(keyPair.getAddress(), keyPair.exportPrivateKey(), BigDecimal.ZERO);
//        if (dbAccess.putAccount(account)) {
//            return new ResultDto(ResultEnum.SUCCESS.getCode(), account);
//        }
//        return new ResultDto(ResultEnum.SYS_ERROR);

        Account account = accountHandler.newAccount(password);
        if(account != null){
            return new ResultDto(ResultEnum.SUCCESS.getCode(), account);
        }
        return new ResultDto(ResultEnum.SYS_ERROR);

    }

    @GetMapping("/setMinerAccount")
    public ResultDto setMinerAccount(HttpServletRequest request) throws Exception {
        String address = request.getParameter("address");
        if(address != null && !"".equalsIgnoreCase(address)) {
            Account account = accountHandler.setMinerAccount(address);
            if (account != null) {
                return new ResultDto(ResultEnum.SUCCESS.getCode(), account);
            }
        }
        return new ResultDto(ResultEnum.SYS_ERROR);
    }
}
