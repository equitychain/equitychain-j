package com.passport.peer;

import com.passport.heartbeat.HeartBeatClientHandler;
import com.passport.heartbeat.HeartBeatServerHandler;
import com.passport.proto.NettyMessage;
import com.passport.utils.HttpUtils;
import com.passport.zookeeper.ServiceRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class ConnectAsync {

  private static final Logger logger = LoggerFactory.getLogger(ConnectAsync.class);

  @Autowired
  private ServiceRegistry serviceRegistry;
  @Autowired
  private ClientHandler clientHandler;
  @Autowired
  private ServerHandler serverHandler;
  @Autowired
  private HeartBeatClientHandler heartBeatClientHandler;
  @Autowired
  private HeartBeatServerHandler heartBeatServerHandler;
  @Autowired
  private ChannelsManager channelsManager;

  @Value("${rpc.serverPort}")
  private int serverPort;

  @Async("taskAsyncPool4Server")
  public void startServer() {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
              ChannelPipeline pipeline = sc.pipeline();
              pipeline.addLast(new ProtobufVarint32FrameDecoder());
              pipeline.addLast(new ProtobufDecoder(NettyMessage.Message.getDefaultInstance()));
              pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
              pipeline.addLast(new ProtobufEncoder());
              pipeline.addLast(serverHandler);
              pipeline.addLast(heartBeatServerHandler);
            }
          })
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.TCP_NODELAY, true);

      ChannelFuture f = b.bind(serverPort).sync();

      String serviceAddress = HttpUtils.getLocalHostLANAddress().getHostAddress();
      logger.info("discover：{}", serviceAddress);
      serviceRegistry.register(serviceAddress);

      f.channel().closeFuture().sync();
    } catch (Exception e) {
      logger.error("error", e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  @Async("taskAsyncPool4Client")
  public void startConnect(String address) {
    ChannelFuture cf = null;
    EventLoopGroup workgroup = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(workgroup)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
              ChannelPipeline pipeline = sc.pipeline();
              pipeline.addLast(new ProtobufVarint32FrameDecoder());
              pipeline.addLast(new ProtobufDecoder(NettyMessage.Message.getDefaultInstance()));
              pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
              pipeline.addLast(new ProtobufEncoder());
              pipeline.addLast(clientHandler);
              pipeline.addLast(heartBeatClientHandler);
            }
          });

      cf = b.connect(address, serverPort).sync();

      cf.channel().closeFuture().sync();
    } catch (Exception e) {
      logger.error("error", e);
    } finally {
      logger.info("contains:" + channelsManager.getChannels().contains(cf.channel()));
      logger.info("remove:" + channelsManager.getChannels().remove(cf.channel()));

      logger.info("reconnect");
      try {
        TimeUnit.SECONDS.sleep(5);
        try {
          startConnect(address);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
