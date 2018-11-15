package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.annotations.RocksTransaction;
import com.passport.constant.Constant;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.BlockMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.BlockHandler;
import com.passport.webhandler.TransactionHandler;
import com.passport.webhandler.TrusteeHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
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
    @Lazy
    private BlockHandler blockHandler;
    @Autowired
    private TransactionHandler transactionHandler;
    @Autowired
    private ApplicationContextProvider provider;
    @Autowired
    private TrusteeHandler trusteeHandler;
    @Autowired
    private BlockUtils blockUtils;

    private Lock lock = new ReentrantLock();
    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块广播请求数据：{}", GsonUtils.toJson(message));
        //接收到消息停止定时任务中的重选
        SyncFlag.blockSyncFlag = false;
        SyncFlag.timerFlag = true;
        try {
            lock.lock();
            BlockMessage.Block block = message.getData().getBlock();

            if(SyncFlag.isNextBlockSyncFlag()){
            logger.info("正在主动同步区块，暂时不处理流水广播消息");
                return;
             }

            //区块同步需要按顺序进行
            Optional<Object> lastBlockHeightOptional = dbAccess.getLastBlockHeight();
            if (!lastBlockHeightOptional.isPresent()){
                return;
            }
            long lastBlockHeight = CastUtils.castLong(lastBlockHeightOptional.get());
            long blockHeight = block.getBlockHeight();
            if((lastBlockHeight + 1) != blockHeight){//最后的区块高度+1=广播过来的区块高度，表示区块按顺序处理
                logger.info("本地区块最新高度和广播过来的区块高度相差!=1");
                //如果本地高度和广播过来的区块高度差
                if(blockHeight - lastBlockHeight >= Constant.BLOCK_HEIGHT_GAP){
                    logger.info("本地区块最新高度和广播过来的区块高度相差>=30");
                    //修改主动同步标记
                    SyncFlag.setNextBlockSyncFlag(true);
                    //发布主动同步事件
                    provider.publishEvent(new SyncNextBlockEvent(0L));
                }
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
            //出块完成后，计算出的下一个出块人如果是自己则继续发布出块事件
            int blockCycle = blockUtils.getBlockCycle(blockLocal.getBlockHeight());
            //初始化受托人列表
            List<Trustee> trustees = trusteeHandler.findValidTrustees(blockCycle);
            if(trustees.size() == 0){
                trusteeHandler.getTrusteesBeforeTime(blockLocal.getBlockHeight(), blockCycle);
            }
            //改变状态
            Optional<Trustee> trusteeOpt = dbAccess.getTrustee(blockLocal.getProducer());

            if(trusteeOpt.isPresent()) {
                trusteeHandler.changeStatus(trusteeOpt.get(),blockCycle);
            }
            //打包流水成功后，判断下个出块人是否本节点
            System.out.println("==============收到区块，检测下个出块人是不是我出块===========");
            blockHandler.produceNextBlock();
        } catch (Exception e) {
            logger.error("接收区块广播异常", e);
        } finally {
            lock.unlock();
        }
    }
}
