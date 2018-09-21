package com.passport.web;

import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        String tradeType = request.getParameter("tradeType");
        boolean flag =false;
        //若流水类型为 委托人注册 或 投票人注册的时候 不校验receiptAddress
        if(TransactionTypeEnum.TRUSTEE_REGISTER.getDesc().equals(tradeType)
                ||TransactionTypeEnum.VOTER_REGISTER.getDesc().equals(tradeType)){
            flag = CheckUtils.checkParamIfEmpty(payAddress, value, extarData);
        }else{
            //非空检验
            flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
        }

        if(flag){
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }

        Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password, tradeType);
        return new ResultDto(ResultEnum.SUCCESS.getCode(), transaction);
    }
    @GetMapping("/getAllTrans")
    public ResultDto getAllTrans(HttpServletRequest request) throws Exception {
        List<Transaction> transactionList = dbAccess.getAllTrans();
        return new ResultDto(ResultEnum.SUCCESS.getCode(),transactionList);
    }
}
