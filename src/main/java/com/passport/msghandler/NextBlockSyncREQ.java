package com.passport.msghandler;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.*;
import com.passport.utils.CastUtils;
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
@Component("DATA_REQ_NEXT_BLOCK_SYNC")//TODO 这里后期要优化为使用常量代替
public class NextBlockSyncREQ extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(NextBlockSyncREQ.class);

    @Autowired
    private DBAccess dbAccess;
    @Autowired
    private BlockHandler blockHandler;

    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块同步请求数据：{}", GsonUtils.toJson(message));

        BlockMessage.Block blockInfo = message.getData().getBlock();
        //查询本地是否有此高度的区块
        long blockHeight = blockInfo.getBlockHeight();
        long count = blockInfo.getBlockSize();
        Optional<Object> lastBlockHeight = dbAccess.getLastBlockHeight();
        //同步返回的区块数量
        if (lastBlockHeight.isPresent()) {
            long maxHeight = CastUtils.castLong(lastBlockHeight.get());
            if(maxHeight < blockHeight){
                return;
            }
            count = count > (maxHeight - blockHeight+1)?(maxHeight - blockHeight+1):count;
        }else{
            return;
        }
        //构造区块同步响应消息
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.NEXT_BLOCK_SYNC);
        //循环添加需要的高度区块
        while(count > 0) {
            Optional<Block> blockOptional = dbAccess.getBlock(blockHeight+count-1);
            if (blockOptional.isPresent()) {
                Block block = blockOptional.get();
                BlockMessage.Block.Builder blockBuilder = blockHandler.convertBlock2BlockMessage(block);
                dataBuilder.addBlocks(blockBuilder);
            }
            count --;
        }
        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
        builder.setData(dataBuilder.build());
        ctx.writeAndFlush(builder.build());
    }
}
