package com.passport.web;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.webhandler.MinerHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 区块
 * 处理区块web接口请求
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
        Block block = blockOptional.get();

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

        return new ResultDto(ResultEnum.SUCCESS.getCode(), newBlock);
    }

    /**
     * 根据区块高度获取流水列表
     * @return
     */
    @GetMapping("getTransactionsByBlockHeight/{blockHeight}")
    public ResultDto<?> getTransactionsByBlockHeight(@PathVariable(value="blockHeight") long blockHeight) {
        List<Transaction> transactions = dbAccess.getTransactionsByBlockHeight(blockHeight);
        return new ResultDto(ResultEnum.SUCCESS.getCode(), transactions);
    }

    /**
     * 分页查询区块
     * @return
     */
    @GetMapping("getBlockByPage/{pageNum}")
    public ResultDto<?> getBlockByPage(@PathVariable(value="pageNum") int pageNum) {
        try {
            List<Block> blocks = dbAccess.blockPagination(10, pageNum);
            return new ResultDto(ResultEnum.SUCCESS.getCode(), blocks);
        } catch (Exception e) {
            return new ResultDto(ResultEnum.SYS_ERROR);
        }
    }
}
