package com.passport.web;

import com.passport.core.Account;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.utils.CheckUtils;
import com.passport.utils.LockUtil;
import com.passport.utils.StoryFileUtil;
import com.passport.webhandler.AccountHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
    @Autowired
    StoryFileUtil fileUtil;
//    @Autowired
//    DBAccess dbAccess;
//    @Value("${wallet.keystoreDir}")
//    private String walletDir;

    @Value("${wallet.keystoreDir}")
    private String walletDir;

    @GetMapping("/new")
    public ResultDto newAccount(HttpServletRequest request) throws Exception {

        String password = request.getParameter("password");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(password);
        if(flag){
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }

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

    //解锁账号
    @GetMapping("/unlock")
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
    }
}
