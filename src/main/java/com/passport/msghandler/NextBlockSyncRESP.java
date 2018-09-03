package com.passport.msghandler;

import com.passport.core.Block;
import com.passport.db.dbhelper.DBAccess;
import com.passport.event.SyncNextBlockEvent;
import com.passport.listener.ApplicationContextProvider;
import com.passport.proto.BlockMessage;
import com.passport.proto.NettyMessage;
import com.passport.webhandler.BlockHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("DATA_RESP_NEXT_BLOCK_SYNC")
public class NextBlockSyncRESP extends Strategy {

  private static final Logger logger = LoggerFactory.getLogger(NextBlockSyncRESP.class);

  @Autowired
  private DBAccess dbAccess;
  @Autowired
  private ApplicationContextProvider provider;
  @Autowired
  private BlockHandler blockHandler;

  @Override
  public void handleRespMsg(ChannelHandlerContext ctx, NettyMessage.Message message) {

    BlockMessage.Block block = message.getData().getBlock();
    Block blockLocal = blockHandler.convertBlockMessage2Block(block);

    if (dbAccess.getBlock(blockLocal.getBlockHeight()).isPresent()) {
      return;
    }
    if (!blockHandler.checkBlock(blockLocal)) {
      return;
    }

    dbAccess.putBlock(blockLocal);
    dbAccess.putLastBlockHeight(blockLocal.getBlockHeight());

    provider.publishEvent(new SyncNextBlockEvent(0L));
  }
}
