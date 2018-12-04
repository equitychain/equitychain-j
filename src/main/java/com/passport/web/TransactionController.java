package com.passport.web;

import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.exception.CommonException;
import com.passport.utils.CheckUtils;
import com.passport.utils.DateUtils;
import com.passport.webhandler.TransactionHandler;
import org.rocksdb.RocksDBException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResultDto send(HttpServletRequest request) {
        try {
            if(SyncFlag.isNextBlockSyncFlag()){
                return new ResultDto(ResultEnum.TRANS_UNCOMPSYN);
            }
            String payAddress = request.getParameter("payAddress");
            String receiptAddress = request.getParameter("receiptAddress");
            String value = request.getParameter("value");
            String extarData = request.getParameter("extarData");
            String password = request.getParameter("password");
            String tradeType = request.getParameter("tradeType");
            String token = request.getParameter("token");
            boolean flag = false;
            //若流水类型为 委托人注册 或 投票人注册的时候 不校验receiptAddress
            if (TransactionTypeEnum.TRUSTEE_REGISTER.toString().equals(tradeType)
                    || TransactionTypeEnum.VOTER_REGISTER.toString().equals(tradeType)) {
                flag = CheckUtils.checkParamIfEmpty(payAddress, value, extarData);
            } else {
                //非空检验
                flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
            }

            if (flag) {
                return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
            }

            Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password, tradeType,token);
            com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, transactionDto);
            return new ResultDto(ResultEnum.SUCCESS.getCode(), transactionDto);
        }catch (CommonException e){
            e.printStackTrace();
            return new ResultDto(e.getResultEnum().getCode(),e.getMessage());
        }

    }

    /**
     * 根据地址查流水
     *
     * @param pageCount
     * @param pageNumber
     * @param address
     * @return
     */
    @GetMapping("getTransactionByAddress")
    public ResultDto getTransactionByAddress(@RequestParam("pageCount") int pageCount,
                                             @RequestParam("pageNumber") int pageNumber, @RequestParam("address") String address) {
        try {
            Long t1 = System.currentTimeMillis();
            List<String> screens = new ArrayList<>();
            List<byte[][]> screenVals = new ArrayList<>();
            Integer lashBlockHeight = Integer.valueOf(dbAccess.getLastBlockHeight().get().toString());
            screens.add("payAddress");
            screens.add("receiptAddress");
            byte[][] bytes1 = new byte[1][];
            bytes1[0] = address.getBytes();
            screenVals.add(bytes1);
            screenVals.add(bytes1);
            List<Transaction> transactions = dbAccess.transactionPagination(pageCount, pageNumber, 0, screens, screenVals, 1);
            Long t2 = System.currentTimeMillis();
            System.out.println(t2-t1);
            List<com.passport.dto.coreobject.Transaction> transactionsDto = new ArrayList<>();
            for (Transaction transaction : transactions) {
                com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
                BeanUtils.copyProperties(transaction, transactionDto);
                transactionDto.setConfirms(lashBlockHeight-Integer.valueOf(transactionDto.getBlockHeight().toString()));
                BigDecimal eggUsed = transactionDto.getEggUsed()==null||transactionDto.getEggUsed().equals("") ? BigDecimal.ZERO : new BigDecimal(transactionDto.getEggUsed().toString());
                BigDecimal eggPrice = transactionDto.getEggPrice()==null||transactionDto.getEggPrice().equals("")  ? BigDecimal.ZERO : new BigDecimal(transactionDto.getEggPrice().toString());
                BigDecimal fee = eggPrice.multiply(eggUsed).setScale(8, BigDecimal.ROUND_HALF_UP);
                transactionDto.setFee(fee);
                transactionDto.setToken(Constant.MAIN_COIN);
                transactionsDto.add(transactionDto);
            }
            Map resultMap = new HashMap();
            resultMap.put("transactionList", transactionsDto);
//            resultMap.put("count", dbAccess.getTransCountByAddress(address));//TODO:待解决
            resultMap.put("count",transactionsDto.size());
            ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
            resultDto.setData(resultMap);
            return resultDto;
        }catch (CommonException e){
            e.printStackTrace();
            return new ResultDto(e.getResultEnum().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResultDto(ResultEnum.SYS_ERROR.getCode(),e.getMessage());
        }
    }

    /**
     * 查询前n个区块的流水
     *
     * @param pageCount
     * @param pageNumber
     * @param nBlock
     * @return
     */
    @GetMapping("getTransactionByNBlock")
    public ResultDto getTransactionByNBlock(@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber, @RequestParam("nBlock") int nBlock) {
        Integer lashBlockHeight = Integer.valueOf(dbAccess.getLastBlockHeight().get().toString());
        nBlock = nBlock >lashBlockHeight  ?  lashBlockHeight : nBlock;
        List<Transaction> transactionAll = dbAccess.getNewBlocksTransactions(1000 * nBlock, 1, nBlock);
        List<Transaction> transactions = dbAccess.getNewBlocksTransactions(pageCount, pageNumber, nBlock);
        List<com.passport.dto.coreobject.Transaction> transactionsDto = new ArrayList<>();
        for (Transaction transaction : transactions) {
            com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, transactionDto);
            transactionDto.setConfirms(lashBlockHeight-Integer.valueOf(transactionDto.getBlockHeight().toString()));
            BigDecimal eggUsed = transactionDto.getEggUsed()==null||transactionDto.getEggUsed().equals("") ? BigDecimal.ZERO : new BigDecimal(transactionDto.getEggUsed().toString());
            BigDecimal eggPrice = transactionDto.getEggPrice()==null||transactionDto.getEggPrice().equals("")  ? BigDecimal.ZERO : new BigDecimal(transactionDto.getEggPrice().toString());
            BigDecimal fee = eggPrice.multiply(eggUsed).setScale(8, BigDecimal.ROUND_HALF_UP);
            transactionDto.setFee(fee);
            transactionDto.setToken(Constant.MAIN_COIN);
            transactionsDto.add(transactionDto);
        }
        Map resultMap = new HashMap();
        resultMap.put("transactionList", transactionsDto);
        resultMap.put("count", transactionAll.size());
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(resultMap);
        return resultDto;
    }

    /**
     * 根据区块高度查询流水
     * @param blockHeight
     * @return
     */
    @GetMapping("getTransactionByBlockHeight")
    public ResultDto getTransactionByBlockHeight(@RequestParam("blockHeight") int blockHeight) {
        List<Transaction> transactions = dbAccess.getTransactionsByBlockHeight(blockHeight);
        List<com.passport.dto.coreobject.Transaction> transactionsDto = new ArrayList<>();
        for (Transaction transaction : transactions) {
            com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, transactionDto);
            transactionsDto.add(transactionDto);
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactionsDto);
        return resultDto;
    }
    @GetMapping("getTransactionList")
    public ResultDto getTransactionList(){
        List<com.passport.dto.coreobject.Transaction> transactions = new ArrayList<>();
        Long t1 = System.currentTimeMillis();
        List<Transaction> transactionList = dbAccess.listTransactions();
        Long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        for(Transaction transaction:transactionList){
            com.passport.dto.coreobject.Transaction tranObj = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, tranObj);
            tranObj.setTime(DateUtils.stampToDate(tranObj.getTime()+""));
            transactions.add(tranObj);
        }
        return new ResultDto(ResultEnum.SUCCESS.getCode(),transactions);
    }
}
