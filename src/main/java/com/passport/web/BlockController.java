package com.passport.web;

<<<<<<< HEAD
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.msghandler.BlockSyncREQ;
import com.passport.utils.DateUtils;
import com.passport.webhandler.MinerHandler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.RocksDBException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

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

    /**
     * 查询区块列表
     *
     * @param pageCount     :每页记录数
     * @param pageNumber：页码
     * @return
     */
    @GetMapping("getBlockList")
    public ResultDto getBlockList(@RequestParam("pageCount") int pageCount, @RequestParam("pageNumber") int pageNumber) {
        List<Block> blocks = new ArrayList<>();
        Map resultMap = new HashMap();
        List<com.passport.dto.coreobject.Block> newBlocks = new ArrayList<>();
        long lastBlockHeight = 0;
        try {
            lastBlockHeight = Long.valueOf(dbAccess.getLastBlockHeight().get().toString());
            blocks = dbAccess.blockPagination(pageCount, pageNumber);
            blocks.forEach(block -> {
                newBlocks.add(getBlockObject(block));
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.SYS_ERROR);
        }
        resultMap.put("blockList", newBlocks);
        resultMap.put("count", lastBlockHeight);
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(resultMap);
        return resultDto;
    }

    /**
     * 根据区块高度查询区块信息
     *
     * @param blockHeight:区块高度
     * @return
     */
    @GetMapping("getBlockByHeight")
    public ResultDto getBlockByHeight(@RequestParam("blockHeight") int blockHeight) {
        long lastBlockHeight = 0;
        Optional<Object> objectOptional = dbAccess.getLastBlockHeight();
        if (objectOptional.isPresent()) {
            lastBlockHeight = Long.valueOf(objectOptional.get().toString());
        }
        com.passport.dto.coreobject.Block newBlock = new com.passport.dto.coreobject.Block();
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        if (blockOptional.isPresent()) {
            newBlock = getBlockObject(blockOptional.get());
            for (com.passport.dto.coreobject.Transaction transaction : newBlock.getTransactions()) {
                totalValue = totalValue.add(transaction.getValue() == null ? BigDecimal.ZERO : new BigDecimal(transaction.getValue().toString()));
                BigDecimal eggUsed = transaction.getEggUsed()==null||transaction.getEggUsed().equals("") ? BigDecimal.ZERO : new BigDecimal(transaction.getEggUsed().toString());
                BigDecimal eggPrice = transaction.getEggPrice()==null||transaction.getEggPrice().equals("")  ? BigDecimal.ZERO : new BigDecimal(transaction.getEggPrice().toString());
                BigDecimal fee = eggPrice.multiply(eggUsed).setScale(8, BigDecimal.ROUND_HALF_UP);
                totalValue = totalValue.add(fee);
                transaction.setConfirms(lastBlockHeight - blockHeight);
                transaction.setFee(fee);
                transaction.setToken(Constant.MAIN_COIN);
            }
            newBlock.setTotalValue(totalValue);
            newBlock.setTotalFee(totalFee);
        }
        ResultDto resultDto = new ResultDto(ResultEnum.SUCCESS);
        resultDto.setData(newBlock);
        return resultDto;
    }
    /**
     * 根据区块高度 流水页数 获取单条流水
     *
     * @param blockHeight:区块高度
     * @return
     */
    @GetMapping("getBlockByHeightTransaction")
    public ResultDto getBlockByHeightTransaction(@RequestParam("blockHeight") int blockHeight,@RequestParam(value = "pageNum") int pageNum) {
        long lastBlockHeight = 0;
        Optional<Object> objectOptional = dbAccess.getLastBlockHeight();
        if (objectOptional.isPresent()) {
            lastBlockHeight = Long.valueOf(objectOptional.get().toString());
        }
        com.passport.dto.coreobject.Block newBlock = new com.passport.dto.coreobject.Block();
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        if (blockOptional.isPresent()) {
            newBlock = getBlockObject(blockOptional.get());
            com.passport.dto.coreobject.Transaction transaction = newBlock.getTransactions().get(pageNum-1);
            totalValue = totalValue.add(transaction.getValue() == null ? BigDecimal.ZERO : new BigDecimal(transaction.getValue().toString()));
            BigDecimal eggUsed = transaction.getEggUsed()==null||transaction.getEggUsed().equals("") ? BigDecimal.ZERO : new BigDecimal(transaction.getEggUsed().toString());
            BigDecimal eggPrice = transaction.getEggPrice()==null||transaction.getEggPrice().equals("")  ? BigDecimal.ZERO : new BigDecimal(transaction.getEggPrice().toString());
            BigDecimal fee = eggPrice.multiply(eggUsed).setScale(8, BigDecimal.ROUND_HALF_UP);
            totalValue = totalValue.add(fee);
            transaction.setConfirms(lastBlockHeight - blockHeight);
            transaction.setFee(fee);
            transaction.setToken(Constant.MAIN_COIN);
            return new ResultDto(ResultEnum.SUCCESS.getCode(),transaction);
        }else{
            return new ResultDto(ResultEnum.SYS_ERROR);
        }
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
     *
     * @return
     */
    @GetMapping("getBlockByHeight/{blockHeight}")
    @ResponseBody
    public ResultDto<?> getBlockByHeight(@PathVariable(value = "blockHeight") long blockHeight) {
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        com.passport.dto.coreobject.Block newBlock = getBlockObject(blockOptional.get());

        return new ResultDto(ResultEnum.SUCCESS.getCode(), newBlock);
    }

    /**
     * 反射取byte数组对应的数据
     *
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
     *
     * @return
     */
    @GetMapping("getTransactionsByBlockHeight/{blockHeight}")
    public ResultDto<?> getTransactionsByBlockHeight(@PathVariable(value = "blockHeight") long blockHeight) {
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
     *
     * @return
     */
    @GetMapping("getBlockByPage/{pageNum}")
    public ResultDto<?> getBlockByPage(@PathVariable(value = "pageNum") int pageNum) {
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

    @GetMapping("getBlocksByHeight/{blockHeight}-{blockCount}")
    @ResponseBody
    public List<com.passport.dto.coreobject.Block> getBlocksByHeight(@PathVariable("blockHeight") int blockHeight, @PathVariable("blockCount") int blockCount) {
        List<com.passport.dto.coreobject.Block> list = new ArrayList<>();
        try {
            dbAccess.getBlocksByHeight(blockHeight, blockCount).forEach(block -> {
                list.add(getBlockObject(block));
            });
        } catch (Exception e) {

        }
        return list;
    }
    //统计
    @GetMapping("getCensesData")
    @ResponseBody
    public ResultDto getCensesData(){
        try {
            return new ResultDto(ResultEnum.SUCCESS.getCode(), dbAccess.censesData());
        } catch (RocksDBException e) {
            e.printStackTrace();
            return new ResultDto(ResultEnum.SYS_ERROR);
        }
    }

    /**
     * 启动时候的轮训
     * @return
     */
    @GetMapping("getSyncBlockSchedule")
    @ResponseBody
    public ResultDto getSyncBlockSchedule(){
        Map<String,Object> map = new HashMap<>();
        map.put("BlockSync",SyncFlag.isNextBlockSyncFlag());
        Optional<Object> objectOptional = dbAccess.getLastBlockHeight();
        Long lastBlockHeight = 0l;
        if (objectOptional.isPresent()) {
            lastBlockHeight = Long.valueOf(objectOptional.get().toString());
        }
        BigDecimal blockSchedule = new BigDecimal((double) lastBlockHeight / SyncFlag.blockHeight);
        map.put("blockSchedule",blockSchedule.setScale(2,BigDecimal.ROUND_HALF_UP));
        return new ResultDto(ResultEnum.SUCCESS.getCode(),map);
    }

    /**
     * K线图
     * @return
     */
    @GetMapping("getKChart")
    @ResponseBody
    public ResultDto getKChart(){
        List<com.passport.dto.coreobject.Transaction> transactions = new ArrayList<>();
        List<Transaction> transactionList = dbAccess.listTransactions();
        for(Transaction transaction:transactionList){
            com.passport.dto.coreobject.Transaction tranObj = new com.passport.dto.coreobject.Transaction();
            BeanUtils.copyProperties(transaction, tranObj);
            transactions.add(tranObj);
        }
        Long one = new Long(transactions.get(0).getTime().toString());
        List<K> list = new ArrayList<>();
        int i = 0;
        for(com.passport.dto.coreobject.Transaction transaction:transactions){
            if(one<= new Long(transaction.getTime().toString()) && new Long(transaction.getTime().toString()) <= one + 60*60*1000 ){//测试每小时统计
                i++;
            }else{
//                sum.put(DateUtils.stampToDate(transaction.getTime().toString())+"_"+DateUtils.stampToDate( (new Long(transaction.getTime().toString())+60*60*1000)+"" ), i);
                list.add(new K(DateUtils.stampToDate(transaction.getTime().toString()),i));
                one = new Long(transaction.getTime().toString());
                i = 0;
            }
        }
        //刚启动只有一小时内
        if(list.size() == 0){
            list.add(new K(DateUtils.stampToDate(one.toString()),i));
        }
        System.out.println(JSONObject.toJSONString(list));
        return new ResultDto(ResultEnum.SUCCESS.getCode(),list);
    }
    class K{
        private String date;
        private int sum;

        public K(String date, int sum) {
            this.date = date;
            this.sum = sum;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }
=======
import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.webhandler.MinerHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
