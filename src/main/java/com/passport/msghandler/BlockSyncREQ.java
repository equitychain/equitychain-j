package com.passport.msghandler;

import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.*;
import com.passport.utils.GsonUtils;
import com.passport.utils.eth.ByteUtil;
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

    public void handleReqMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {
        logger.info("处理区块同步请求数据：{}", GsonUtils.toJson(message));

        //查询本地是否有此高度的区块
        long blockHeight = message.getData().getBlock().getBlockHeight();
        Optional<Block> blockOptional = dbAccess.getBlock(blockHeight);
        if(blockOptional.isPresent()){
            Block block = blockOptional.get();

            //构造区块头
            BlockHeaderMessage.BlockHeader.Builder blockHeaderBuilder = BlockHeaderMessage.BlockHeader.newBuilder();
            blockHeaderBuilder.setTimeStamp(block.getBlockHeader().getTimeStamp());
            blockHeaderBuilder.setHashPrevBlock(ByteString.copyFrom(block.getBlockHeader().getHashMerkleRoot()));
            blockHeaderBuilder.setHashMerkleRoot(ByteString.copyFrom(block.getBlockHeader().getHashMerkleRoot()));
            blockHeaderBuilder.setHash(ByteString.copyFrom(block.getBlockHeader().getHash()));

            //构造区块
            BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
            blockBuilder.setBlockSize(block.getBlockSize());
            blockBuilder.setBlockHeader(blockHeaderBuilder);
            blockBuilder.setTransactionCount(block.getTransactionCount());
            blockBuilder.setBlockHeight(block.getBlockHeight());
            //设置包含在区块中的流水记录
            block.getTransactions().forEach((Transaction trans) -> {
                TransactionMessage.Transaction.Builder transactionBuilder = TransactionMessage.Transaction.newBuilder();
                transactionBuilder.setHash(ByteString.copyFrom(trans.getHash()));
                transactionBuilder.setSignature(ByteString.copyFrom(trans.getSignature()));
                transactionBuilder.setValue(ByteString.copyFrom(trans.getSignature()));
                transactionBuilder.setPayAddress(ByteString.copyFrom(trans.getPayAddress()));
                transactionBuilder.setReceiptAddress(ByteString.copyFrom(trans.getReceiptAddress()));
                transactionBuilder.setEggPrice(ByteString.copyFrom(trans.getEggPrice()));
                transactionBuilder.setEggMax(ByteString.copyFrom(trans.getEggMax()));
                transactionBuilder.setTimeStamp(ByteUtil.byteArrayToLong(trans.getTime()));

                blockBuilder.addTransactions(transactionBuilder);
            });

            //构造区块同步响应消息
            NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
            dataBuilder.setDataType(DataTypeEnum.DataType.BLOCK_SYNC);
            dataBuilder.setBlock(blockBuilder);
            NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
            builder.setData(dataBuilder);
            builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
            ctx.writeAndFlush(builder.build());
        }
    }
}
