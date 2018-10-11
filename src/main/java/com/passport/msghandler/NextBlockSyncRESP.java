package com.passport.msghandler;

import com.passport.annotations.RocksTransaction;
import com.passport.constant.SyncFlag;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.BlockMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.BlockHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端处理区块同步响应
 * @author: xujianfeng
 * @create: 2018-07-18 15:52
 **/
@Component("DATA_RESP_NEXT_BLOCK_SYNC")//TODO 这里后期要优化为使用常量代替
public class NextBlockSyncRESP extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(NextBlockSyncRESP.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private ApplicationContextProvider provider;
    @Autowired
    private BlockHandler blockHandler;
    @Override
    @RocksTransaction
    public void handleMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块同步响应结果：{}", GsonUtils.toJson(message));
        if(blockHandler.padding){
            //正在处理
            return;
        }
        //获取到同步的区块集合
        List<BlockMessage.Block> blocks = message.getData().getBlocksList();
        if(blocks==null || blocks.size() == 0){
            //同步完了，不进行广播，
            SyncFlag.setNextBlockSyncFlag(false);
            return;
        }
        List<Block> blockList = new ArrayList<>();
        for(BlockMessage.Block block : blocks) {
//            BlockMessage.Block block = message.getData().getBlock();
            Block blockLocal = blockHandler.convertBlockMessage2Block(block);

            //本地是否已经存在此高度区块
            if (dbAccess.getBlock(blockLocal.getBlockHeight()).isPresent()) {
                return;
            }
            //验证区块合法性
//            if (!blockHandler.checkBlock(blockLocal)) {
//                return;
//            }
            blockList.add(blockLocal);
        }
        try {
            blockHandler.addBlockQueue(blockList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //存储区块到本地
//        dbAccess.putBlock(blockLocal);
//        dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());

        //继续同步下一个区块
//        provider.publishEvent(new SyncNextBlockEvent(0L));
    }
}
