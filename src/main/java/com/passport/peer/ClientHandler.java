package com.passport.peer;

import com.google.common.base.Optional;
import com.passport.core.Block;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.msghandler.StrategyContext;
import com.passport.proto.NettyMessage;
import com.passport.utils.BlockUtils;
import com.passport.utils.CastUtils;
import com.passport.utils.GsonUtils;
import com.passport.webhandler.TrusteeHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
@Component
public class ClientHandler extends SimpleChannelInboundHandler<NettyMessage.Message> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    private ChannelsManager channelsManager;
    @Autowired
    private StrategyContext strategyContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage.Message message) throws Exception {
        logger.info("client read data客户端读到的数据是：{}", GsonUtils.toJson(message));
        strategyContext.handleMsgMain(ctx, message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client channel active客户端通道激活");

        logger.info("client channel id:"+ctx.channel().id().asLongText());
        //保存连接的channel
        channelsManager.addChannel(ctx.channel());
    }

    @Autowired
    private BaseDBAccess dbAccess;
    @Autowired
    private BlockUtils blockUtils;
    @Autowired
    private TrusteeHandler trusteeHandler;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //重铸机制测试
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        System.out.println(clientIP);
        List<String> removeTrustee =dbAccess.seekByKey("heartbeat"+clientIP);

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
        ctx.close();
    }
}

