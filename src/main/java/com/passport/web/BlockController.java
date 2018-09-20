package com.passport.web;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.MinerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 区块
 * 处理区块web接口请求
 *
 * @author: xujianfeng
 * @create: 2018-07-23 15:41
 **/
@RestController
@RequestMapping("/chain")
public class BlockController {
    @Autowired
    private MinerHandler minerHandler;
    @Autowired
    private DBAccess dbAccess;

    @GetMapping("/mine")
    public ResultDto mine(HttpServletRequest request) throws Exception {
        minerHandler.mining();
        return new ResultDto(ResultEnum.SUCCESS);
    }


    /**
     * 查询区块列表
     *
     * @param request
     * @return
     */
    @GetMapping("getBlockList")
    public ResultDto getBlockList(HttpServletRequest request) {
        String pageSize = request.getParameter("pageSize");
        String pageNumber = request.getParameter("pageNumber");
        List<Block> blocks = new ArrayList<>();
        boolean flag = CheckUtils.checkParamIfEmpty(pageSize, pageNumber);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        try {
            blocks = dbAccess.blockPagination(Integer.valueOf(pageSize), Integer.valueOf(pageNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(blocks);
        return resultDto;
    }

    /**
     * 根据区块高度查询区块信息
     *
     * @param request
     * @return
     */
    @GetMapping("getBlockByHeight")
    public ResultDto getBlockByHeight(HttpServletRequest request) {
        String blockHeight = request.getParameter("blockHeight");
        boolean flag = CheckUtils.checkParamIfEmpty(blockHeight);
        if (flag) {
            return new ResultDto(ResultEnum.PARAMS_LOSTOREMPTY);
        }
        Block block = new Block();
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        if (blockOptional.isPresent()) {
            block = blockOptional.get();
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(block);
        return resultDto;
    }


    /**
     * 获取最新区块高度
     *
     * @return
     */
    @GetMapping("getBlockHeight")
    public ResultDto getBlockHeight() {
        String blockHeight = "0";
        Optional<Object> blockOptional = dbAccess.getLastBlockHeight();
        if (blockOptional.isPresent()) {
            blockHeight = blockOptional.get().toString();
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(blockHeight);
        return resultDto;
    }


}
