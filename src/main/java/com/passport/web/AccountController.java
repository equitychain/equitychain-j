package com.passport.web;

import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
    @Autowired
    private DBAccess dbAccess;

    @GetMapping("/new")
    public ResultDto mine(HttpServletRequest request) throws Exception {
        Account account = new Account();
        account.newAccount();
        boolean flag = dbAccess.putAccount(account);
        if (flag) {
            return new ResultDto(ResultEnum.SUCESS.getCode(), account);
        }
        return new ResultDto(ResultEnum.SYS_ERROR);
    }
}
