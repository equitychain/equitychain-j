package com.passport.web;

import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

    boolean flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
    if (flag) {
      return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
    }

    Transaction transaction = transactionHandler
        .sendTransaction(payAddress, receiptAddress, value, extarData, password);
    return new ResultDto(ResultEnum.SUCCESS.getCode(), transaction);
  }
}
