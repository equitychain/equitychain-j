package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
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

    @Override
    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块同步请求数据：{}", GsonUtils.toJson(message));

        //查询本地是否有此高度的区块
        long blockHeight = message.getData().getBlock().getBlockHeight();
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        if(blockOptional.isPresent()){
            Block block = blockOptional.get();

            //返回请求的区块
            BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
            blockBuilder.setBlockHeight(block.getBlockHeight());
            blockBuilder.setBlockSize(block.getBlockSize());
            //blockBuilder.setTransactionCount(block.getTransactionCount());

            NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
            dataBuilder.setDataType(DataTypeEnum.DataType.BLOCK_SYNC);

            NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
            builder.setData(dataBuilder);
            builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
            ctx.writeAndFlush(builder.build());
        }
    }
}
