package com.passport.web;

import com.passport.core.Transaction;
import com.passport.core.Trustee;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 委托人
 * 处理区块web接口请求
 * @author: linqihong
 * @create: 2018-09-26
 **/
@RestController
@RequestMapping("/trustee")
public class TrusteeController {
    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private TransactionHandler transactionHandler;

    /**
     * 查询委托人列表
     * @param request
     * @return
     */
    @GetMapping("getTrusteeList")
    public ResultDto getTransactionByNBlock(HttpServletRequest request) {
        List<Trustee> transactions = dbAccess.listTrustees(1000);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactions);
        return resultDto;
    }

}
