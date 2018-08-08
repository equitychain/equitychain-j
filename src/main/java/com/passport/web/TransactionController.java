package com.passport.web;

import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 交易
 * 处理区块web接口请求
 * @author: xujianfeng
 * @create: 2018-07-23 15:41
 **/
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private TransactionHandler transactionHandler;

    @PostMapping("/send")
    public ResultDto send(HttpServletRequest request) throws Exception {
        String payAddress = request.getParameter("payAddress");
        String receiptAddress = request.getParameter("receiptAddress");
        String value = request.getParameter("value");
        String extarData = request.getParameter("extarData");
        String password = request.getParameter("password");

        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
        if(flag){
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }

        Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password);
        return new ResultDto(ResultEnum.SUCCESS.getCode(), transaction);
    }
}
