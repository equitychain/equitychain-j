package com.passport.web;

import com.google.common.base.Optional;
import com.passport.core.Block;

import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.utils.CheckUtils;
import com.passport.webhandler.MinerHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 根据区块高度获取区块
     * @return
     */
    @GetMapping("getBlockByHeight/{blockHeight}")
    @ResponseBody
    public ResultDto<?> getBlockByHeight(@PathVariable(value="blockHeight") long blockHeight) {
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        com.passport.dto.coreobject.Block newBlock = getBlockObject(blockOptional.get());

        return new ResultDto(ResultEnum.SUCCESS.getCode(), newBlock);
    }

    /**
     * 反射取byte数组对应的数据
     * @param block
     * @return
     */
    @NotNull
    private com.passport.dto.coreobject.Block getBlockObject(Block block) {
        com.passport.dto.coreobject.Block newBlock = new com.passport.dto.coreobject.Block();
        BeanUtils.copyProperties(block, newBlock);

        com.passport.dto.coreobject.BlockHeader newBlockHeader = new com.passport.dto.coreobject.BlockHeader();
        BeanUtils.copyProperties(block.getBlockHeader(), newBlockHeader);
        newBlock.setBlockHeader(newBlockHeader);

        List<com.passport.dto.coreobject.Transaction> newTransactions = new ArrayList<>();
        block.getTransactions().forEach(transaction -> {
            com.passport.dto.coreobject.Transaction newTransaction = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, newTransaction);
            newTransactions.add(newTransaction);
        });
        newBlock.setTransactions(newTransactions);
        return newBlock;
    }

    /**
     * 根据区块高度获取流水列表
     * @return
     */
    @GetMapping("getTransactionsByBlockHeight/{blockHeight}")
    public ResultDto<?> getTransactionsByBlockHeight(@PathVariable(value="blockHeight") long blockHeight) {
        List<Transaction> transactions = dbAccess.getTransactionsByBlockHeight(blockHeight);
        List<com.passport.dto.coreobject.Transaction> newTransactions = new ArrayList<>();
        transactions.forEach(transaction -> {
            com.passport.dto.coreobject.Transaction newTransaction = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, newTransaction);
            newTransactions.add(newTransaction);
        });
        return new ResultDto(ResultEnum.SUCCESS.getCode(), newTransactions);
    }

    /**
     * 分页查询区块
     * @return
     */
    @GetMapping("getBlockByPage/{pageNum}")
    public ResultDto<?> getBlockByPage(@PathVariable(value="pageNum") int pageNum) {
        try {
            List<Block> blocks = dbAccess.blockPagination(10, pageNum);
            List<com.passport.dto.coreobject.Block> newBlocks = new ArrayList<>();
            blocks.forEach(block -> {
                newBlocks.add(getBlockObject(block));
            });
            return new ResultDto(ResultEnum.SUCCESS.getCode(), newBlocks);
        } catch (Exception e) {
            return new ResultDto(ResultEnum.SYS_ERROR);
        }
    }

}
