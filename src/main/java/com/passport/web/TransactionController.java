package com.passport.web;

import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
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
 * 交易
 * 处理区块web接口请求
 *
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

        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }

        Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password, tradeType);
        return new ResultDto(ResultEnum.SUCCESS.getCode(), transaction);
    }

    /**
     * 根据from跟to查流水
     * @param request
     * @return
     */
    @GetMapping
    public ResultDto getTransactionByAddress(HttpServletRequest request) {
        String pageSize = request.getParameter("pageSize");
        String pageNumber = request.getParameter("pageNumber");
        String payAddress = request.getParameter("payAddress");
        String receiptAddress = request.getParameter("receiptAddress");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(pageSize, pageNumber, payAddress, receiptAddress);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        List<String> screens = new ArrayList<>();
        List<byte[][]> screenVals = new ArrayList<>();
        screens.add("payAddress");
        screens.add("receiptAddress");
        byte[][] bytes1 = new byte[1][];
        bytes1[1] = payAddress.getBytes();
        screenVals.add(bytes1);
        byte[][] bytes2 = new byte[1][];
        bytes2[1] = receiptAddress.getBytes();
        screenVals.add(bytes2);
        List<Transaction> transactions = dbAccess.transactionPagination(Integer.valueOf(pageSize), Integer.valueOf(pageNumber), 0, screens, screenVals, 1);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactions);
        return resultDto;
    }

    /**
     * 查询前n个区块的流水
     * @param request
     * @return
     */
    @GetMapping
    public ResultDto getTransactionByNBlock(HttpServletRequest request) {
        String pageSize = request.getParameter("pageSize");
        String pageNumber = request.getParameter("pageNumber");
        String nBlock = request.getParameter("nBlock");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(pageSize, pageNumber,nBlock);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        List<Transaction> transactions = dbAccess.getNewBlocksTransactions(Integer.valueOf(pageSize),Integer.valueOf(pageNumber),Integer.valueOf(nBlock));
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactions);
        return resultDto;
    }

    /**
     * 根据区块高度查询流水
     * @param request
     * @return
     */
    @GetMapping
    public ResultDto getTransactionByBlockHeight(HttpServletRequest request) {
        String blockHeight = request.getParameter("blockHeight");
        //非空检验
        boolean flag = CheckUtils.checkParamIfEmpty(blockHeight);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        List<Transaction> transactions = dbAccess.getTransactionsByBlockHeight(Long.valueOf(blockHeight));
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactions);
        return resultDto;
    }
}
