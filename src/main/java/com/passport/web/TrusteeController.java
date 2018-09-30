package com.passport.web;

import com.google.common.base.Optional;
import com.passport.core.*;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.apache.commons.collections4.list.TreeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 委托人列表
     *
     * @param pageCount
     * @param pageNumber
     * @return
     */
    @GetMapping("getTrusteeListAsAddress")
    public ResultDto getTrusteeListAsAddress(@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber, @RequestParam("address") String address) {
        List<Map> trusteeList = new ArrayList<>();
        List<Trustee> trustees = dbAccess.trusteePagination(pageCount, pageNumber, 0, null, null);
        for (Trustee trustee : trustees) {
            boolean isAlreadyVote = false;
            List<VoteRecord> voteRecords = dbAccess.listVoteRecords(address, "payAddress");
            if (voteRecords.size() != 0) {
                for (VoteRecord voteRecord : voteRecords) {
                    if (voteRecord.getReceiptAddress().equals(trustee.getAddress())) {
                        isAlreadyVote = true;
                    }
                }
            }
            Map trusteeMap = new HashMap();
            trusteeMap.put("address", trustee.getAddress());
            trusteeMap.put("voteNum", trustee.getVotes());
            trusteeMap.put("isAlreadyVote",isAlreadyVote );
            trusteeMap.put("generateRate",trustee.getGenerateRate() );
            trusteeMap.put("income",trustee.getIncome() );
            trusteeMap.put("status",trustee.getStatus());
            trusteeList.add(trusteeMap);
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(trusteeList);
        return resultDto;
    }
}
