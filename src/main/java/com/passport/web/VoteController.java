package com.passport.web;

import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.Transaction;
import com.passport.core.Trustee;
import com.passport.core.VoteRecord;
import com.passport.core.Voter;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.exception.CommonException;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

/**
 * 投票
 * 处理区块web接口请求
 * @author: linqihong
 * @create: 2018-09-26
 **/
@RestController
@RequestMapping("/vote")
public class VoteController {
    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private TransactionHandler transactionHandler;

    private static final Logger logger = LoggerFactory.getLogger(VoteController.class);
    /**
     *
     * @param request
     * payAddress
     * extarData
     * password
     * @return
     */
    @PostMapping("/register")
    public ResultDto register(HttpServletRequest request) {
        try {
            if(SyncFlag.isNextBlockSyncFlag()){
                return new ResultDto(ResultEnum.TRANS_UNCOMPSYN);
            }
            String payAddress = request.getParameter("payAddress");
            String receiptAddress = dbAccess.getCentreAccount();
            String value = Constant.FEE_4_REGISTER_VOTER.toString();
            String extarData = request.getParameter("extarData");
            String password = request.getParameter("password");
            String tradeType = TransactionTypeEnum.VOTER_REGISTER.name();
            String token = Constant.MAIN_COIN;
            boolean flag = CheckUtils.checkParamIfEmpty(payAddress, value, extarData);
            if (flag) {
                return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
            }
            Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password, tradeType,token);
            com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, transactionDto);
            logger.info("发送注册投票人请求");
            return new ResultDto(ResultEnum.SUCCESS.getCode(), transactionDto);
        }catch (CommonException e){
            e.printStackTrace();
            return new ResultDto(e.getResultEnum().getCode(),e.getMessage());
        }
    }

    /**
     * 投票
     */
    @PostMapping("/castTrustee")
    public ResultDto castTrustee(HttpServletRequest request) {
        try {
            if(SyncFlag.isNextBlockSyncFlag()){
                return new ResultDto(ResultEnum.TRANS_UNCOMPSYN);
            }
            String payAddress = request.getParameter("payAddress");
            String receiptAddress = request.getParameter("receiptAddress");
            String value = "1";
            String extarData = request.getParameter("extarData");
            String password = request.getParameter("password");
            String tradeType = TransactionTypeEnum.VOTE.name();
            String token = Constant.MAIN_COIN;
            boolean flag = CheckUtils.checkParamIfEmpty(payAddress, receiptAddress, value, extarData);
            if (flag) {
                return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
            }
            Transaction transaction = transactionHandler.sendTransaction(payAddress, receiptAddress, value, extarData, password, tradeType,token);
            com.passport.dto.coreobject.Transaction transactionDto = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, transactionDto);
            logger.info("发送投票请求");
            return new ResultDto(ResultEnum.SUCCESS.getCode(), transactionDto);
        }catch (CommonException e){
            e.printStackTrace();
            return new ResultDto(e.getResultEnum().getCode(),e.getMessage());
        }
    }
    /**
     * 查询投票人列表
     * @param address
     * @return
     */
    @GetMapping("voteList")
    public ResultDto voteList(@RequestParam("address") String address) {
        ResultDto resultDto =new ResultDto();
        Optional<Voter> voterOptional =  dbAccess.getVoter(address);
        if(voterOptional.isPresent()){
            resultDto = new ResultDto(ResultEnum.SUCCESS);
            resultDto.setData(voterOptional.get());
        }
        return resultDto;
    }
//    @GetMapping("votingRecord")
//    public ResultDto votingRecord(@RequestParam("address") String address,@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber){
//        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
//        List<VoteRecord> voters = dbAccess.votingRecord(address, pageCount,pageNumber);
//        resultDto.setData(voters);
//        return resultDto;
//    }
    @GetMapping("votingRecord")
    public ResultDto votingRecord(@RequestParam("address") String address,@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber){
        List<VoteRecord> voteRecords = dbAccess.listVoteRecords(address, "payAddress");
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
