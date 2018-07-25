package com.passport.web;

import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.miner.DPOSMiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理区块web接口请求
 * @author: xujianfeng
 * @create: 2018-07-23 15:41
 **/
@RestController
@RequestMapping("/chain")
public class BlockController {
    @Autowired
    private DPOSMiner dposMiner;

    @GetMapping("/mine")
    public ResultDto mine(HttpServletRequest request) throws Exception {
        dposMiner.mining();
        return new ResultDto(ResultEnum.SUCESS);
    }
}
