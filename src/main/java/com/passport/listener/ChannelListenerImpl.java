package com.passport.listener;

import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.core.Account;
import com.passport.core.AccountIp;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.proto.*;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.TrusteeHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 当通道激活或者关闭的时候进行dpos的相关操作
 */
@Component
public class ChannelListenerImpl implements ChannelListener {

    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private TrusteeHandler trusteeHandler;

    /**
     * 通道激活  进行ip的统计，也就是本节点的账号添加信息以及获取其他节点的账号ip信息
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        List<Account> accounts = dbAccess.getNodeAccountList();
        //返回请求的区块
        NettyData.Data.Builder dataBuilder = NettyData.Data.newBuilder();
        dataBuilder.setDataType(DataTypeEnum.DataType.ACCOUNTIP_SYNC);
        for (Account account : accounts) {
            AccountMessage.Account.Builder builder = AccountMessage.Account.newBuilder();
            //只需要地址信息
            builder.setAddress(ByteString.copyFrom(account.getAddress().getBytes()));
            dataBuilder.addAccounts(builder.build());
        }

        NettyMessage.Message.Builder builder = NettyMessage.Message.newBuilder();
        builder.setData(dataBuilder.build());
        builder.setMessageType(MessageTypeEnum.MessageType.DATA_RESP);
        System.out.println(builder.getData().getAccountsList().size());

        ctx.writeAndFlush(builder.build());
    }

    /**
     * 通道关闭
     * @param ctx
     */
    @Override
    public void channelClose(ChannelHandlerContext ctx) {
        //重铸机制测试
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        System.out.println(clientIP);
        List<String> removeTrustee = dbAccess.seekByKey("heartbeat"+clientIP);

        Optional<Block> lastBlockOptional = dbAccess.getLastBlock();
        if(!lastBlockOptional.isPresent()){
            ctx.close();
        }
        Block block = lastBlockOptional.get();
        long blockHeight = CastUtils.castLong(block.getBlockHeight());
        long newBlockHeight = blockHeight + 1;
        int blockCycle = blockUtils.getBlockCycle(newBlockHeight);
        List<Trustee> trustees = trusteeHandler.findValidTrustees(blockCycle);
        if(trustees.size() == 0){
            trustees = trusteeHandler.getTrusteesBeforeTime(newBlockHeight, blockCycle);
        }
        for(Trustee trustee:trustees){
            for(String removeAddress:removeTrustee){
                if(trustee.getAddress().equals(removeAddress)){
                    trusteeHandler.changeStatus(trustee, blockCycle);
                }
            }
        }
        //重铸机制测试
    }
}
