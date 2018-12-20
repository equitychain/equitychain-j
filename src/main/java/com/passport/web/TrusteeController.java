package com.passport.web;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.*;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.exception.CommonException;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.apache.commons.collections4.list.TreeList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 委托人
 * 处理区块web接口请求
 *
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


    @PostMapping("/register")
    public ResultDto register(HttpServletRequest request) {
        try {
            if(SyncFlag.isNextBlockSyncFlag()){
                return new ResultDto(ResultEnum.TRANS_UNCOMPSYN);
            }
            String payAddress = request.getParameter("payAddress");
            String receiptAddress = dbAccess.getCentreAccount();
            String value = Constant.FEE_4_REGISTER_TRUSTEE.toString();
            String extarData = request.getParameter("extarData");
            String password = request.getParameter("password");
            String tradeType = TransactionTypeEnum.TRUSTEE_REGISTER.name();
            String token = Constant.MAIN_COIN;
            boolean flag = CheckUtils.checkParamIfEmpty(payAddress, value, extarData);
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
     * 查询前n个委托人
     *
     * @param n
     * @return
     */
    @GetMapping("getTrusteeByN")
    public ResultDto getTrusteeByN(@RequestParam("n") int n) {
        List<Trustee> transactions = dbAccess.listTrustees(n);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(transactions);
        return resultDto;
    }


    /**
     * 委托人列表
     *
     * @param pageCount
     * @param pageNumber
     * @return
     */
    @GetMapping("getTrusteeList")
    public ResultDto getTrusteeList(@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber) {
        List<Trustee> trustees = dbAccess.trusteePagination(pageCount, pageNumber, 0, null, null);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(trustees);
        return resultDto;
    }

    /**
     * 委托人地址获取投票记录 别人投给他的记录
     *
     * @param pageCount
     * @param pageNumber
     * @return
     */
    @GetMapping("getTrusteeListAsAddress")
    public ResultDto getTrusteeListAsAddress(@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber, @RequestParam("address") String address) {
        List<VoteRecord> voteRecords = dbAccess.listVoteRecords(address, "receiptAddress");
        voteRecords.sort(new Comparator<VoteRecord>() {
            @Override
            public int compare(VoteRecord o1, VoteRecord o2) {
                return (int) (o1.getTime()-o2.getTime());
            }
        });
        //当页的数据区间的开始索引
        int beginItem = pageCount * (pageNumber - 1);
        //当页的数据区间的结束索引
        int endItem = pageCount * pageNumber;
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(voteRecords.subList(beginItem<=voteRecords.size()-1 ? beginItem:voteRecords.size(),endItem<=voteRecords.size()-1 ? endItem:voteRecords.size()));
        return resultDto;
    }
}
