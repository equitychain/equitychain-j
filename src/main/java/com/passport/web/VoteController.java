package com.passport.web;

import com.google.common.base.Optional;
import com.passport.core.Trustee;
import com.passport.core.VoteRecord;
import com.passport.core.Voter;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.enums.TransactionTypeEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
