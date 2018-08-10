package com.passport.msghandler;

import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.BlockMessage;
import com.passport.proto.NettyMessage;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.BlockHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端处理区块同步请求
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

    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块广播请求数据：{}", GsonUtils.toJson(message));

        logger.info("处理区块同步响应结果：{}", GsonUtils.toJson(message));

        BlockMessage.Block block = message.getData().getBlock();
        Block blockLocal = blockHandler.convertBlockMessage2Block(block);

        //本地是否已经存在此高度区块
        if(dbAccess.getBlock(blockLocal.getBlockHeight()).isPresent()){
            return;
        }
        //验证区块合法性
        if(!blockHandler.checkBlock(blockLocal)){
            return;
        }

        //存储区块到本地
        dbAccess.putBlock(blockLocal);
        dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());
    }
}
