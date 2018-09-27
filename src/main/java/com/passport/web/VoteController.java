package com.passport.web;

import com.google.common.base.Optional;
import com.passport.core.Trustee;
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

}
