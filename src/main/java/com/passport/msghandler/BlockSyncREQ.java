package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.BlockMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.BlockHandler;
import com.passport.webhandler.TransactionHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务端处理区块同步请求
 *
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_REQ_BLOCK_SYNC")//TODO 这里后期要优化为使用常量代替
public class BlockSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(BlockSyncREQ.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private BlockHandler blockHandler;
    @Autowired
    private TransactionHandler transactionHandler;

    private Lock lock = new ReentrantLock();

    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块广播请求数据：{}", GsonUtils.toJson(message));
        try {
            lock.lock();
            BlockMessage.Block block = message.getData().getBlock();

            //区块同步需要按顺序进行
            Optional<Object> lastBlockHeightOptional = dbAccess.getLastBlockHeight();
            if (!lastBlockHeightOptional.isPresent()){
                return;
            }
            long lastBlockHeight = CastUtils.castLong(lastBlockHeightOptional.get());
            if((lastBlockHeight + 1) != block.getBlockHeight()){//最后的区块高度+1=广播过来的区块高度，表示区块按顺序处理
                return;
            }

            Block blockLocal = blockHandler.convertBlockMessage2Block(block);
            //本地是否已经存在此高度区块
            if (dbAccess.getBlock(blockLocal.getBlockHeight()).isPresent()) {
                return;
            }
            //验证区块合法性
            if (!blockHandler.checkBlock(blockLocal)) {
                return;
            }

            //存储区块到本地
            dbAccess.putBlock(blockLocal);
            dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());

            //流水匹配，和区块中流水一样的未确认流水将放到已确认流水中
            transactionHandler.matchUnConfirmTransactions(blockLocal);
        } catch (Exception e) {
            logger.error("接收区块广播异常", e);
        } finally {
            lock.unlock();
        }
    }
}
