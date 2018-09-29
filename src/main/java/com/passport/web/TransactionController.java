package com.passport.web;

import com.passport.annotations.RocksTransaction;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    @RocksTransaction
    public ResultDto send(HttpServletRequest request) throws Exception {
        String payAddress = request.getParameter("payAddress");
        String receiptAddress = request.getParameter("receiptAddress");
        String value = request.getParameter("value");
        String extarData = request.getParameter("extarData");
        String password = request.getParameter("password");
        String tradeType = request.getParameter("tradeType");

        boolean flag =false;
        //若流水类型为 委托人注册 或 投票人注册的时候 不校验receiptAddress
        if(TransactionTypeEnum.TRUSTEE_REGISTER.toString().equals(tradeType)
                ||TransactionTypeEnum.VOTER_REGISTER.toString().equals(tradeType)){
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

    /**
     * 根据地址查流水
     * @param pageCount
     * @param pageNumber
     * @param address
     * @return
     */
    @GetMapping("getTransactionByAddress")
    public ResultDto getTransactionByAddress(@RequestParam("pageCount") int pageCount,@RequestParam("pageNumber") int pageNumber,@RequestParam("address") String address ) {
        List<String> screens = new ArrayList<>();
        List<byte[][]> screenVals = new ArrayList<>();
        screens.add("payAddress");
        screens.add("receiptAddress");
        byte[][] bytes1 = new byte[1][];
        bytes1[0] = address.getBytes();
        screenVals.add(bytes1);
        screenVals.add(bytes1);
        List<Transaction> transactions = dbAccess.transactionPagination(pageCount, pageNumber, 0, screens, screenVals, 1);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactions);
        return resultDto;
    }

    /**
     * 查询前n个区块的流水
     * @param pageCount
     * @param pageNumber
     * @param nBlock
     * @return
     */
    @GetMapping("getTransactionByNBlock")
    public ResultDto getTransactionByNBlock(@RequestParam("pageCount") int pageCount,@RequestParam("pageNumber") int pageNumber,@RequestParam("nBlock") int nBlock) {
        List<Transaction> transactions = dbAccess.getNewBlocksTransactions(pageCount,pageNumber,nBlock);
        List<com.passport.dto.coreobject.Transaction> transactionsDto =new ArrayList<>();
        for (Transaction transaction:transactions){
            com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction,transactionDto);
            transactionsDto.add(transactionDto);
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactionsDto);
        return resultDto;
    }

    /**
     * 根据区块高度查询流水
     * @param request
     * @return
     */
    @GetMapping("getTransactionByBlockHeight")
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
