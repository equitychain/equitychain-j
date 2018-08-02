package com.passport.msghandler;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.BlockMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户端处理区块同步响应
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_RESP_BLOCK_SYNC")//TODO 这里后期要优化为使用常量代替
public class BlockSyncRESP extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(BlockSyncRESP.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;

    @Override
    public void handleRespMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块同步响应结果：{}", GsonUtils.toJson(message));

        BlockMessage.Block block = message.getData().getBlock();
        Block blockLocal = new Block();
        BeanUtils.copyProperties(block, blockLocal);

        //本地是否已经存在此高度区块
        if(dbAccess.getBlock(blockLocal.getBlockHeight()).isPresent()){
            return;
        }
        //验证区块合法性
        if(!checkBlock(blockLocal, dbAccess)){
            return;
        }

        //存储区块到本地
        dbAccess.putBlock(blockLocal);
        dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());

        //继续同步下一个区块
        provider.publishEvent(new SyncNextBlockEvent(0L));
    }

    //校验区块
    public boolean checkBlock(Block block, DBAccess dbAccess) {
        Long blockHeight = block.getBlockHeight();

        //创世区块，不需要校验和前一个区块的hash
        if (blockHeight == 1) {//比对计算出来的hash和传过来的hash是否一致
             return Objects.equal(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
        }

        //后续区块
        if (blockHeight > 1) {
            boolean flag = Objects.equal(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
            if(!flag){
                return false;
            }

            Optional<Block> prevBlock = dbAccess.getBlock(blockHeight - 1);
            if(prevBlock.isPresent()){
                byte[] hashOfPrevBlock = prevBlock.get().getBlockHeader().getHash();//前一个区块hash
                byte[] prevHashOfCurrentBlock = block.getBlockHeader().getHashPrevBlock();//当前区块的前一个区块hash
                if (hashOfPrevBlock.equals(prevHashOfCurrentBlock)) {//前一个区块的hash和当前区块的前一个区块hash是否相等
                    return true;
                }
            }
        }

        return false;
    }
}
