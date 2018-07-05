package com.passport.peer;

import com.passport.proto.PersonModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import javax.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: xujianfeng
 * @create: 2018-07-05 19:04
 **/
@Component
public class ConnectAsync {
    @Async("taskAsyncPool4Server")
    public void startServer() {
        //1 第一个线程组 是用于接收Client端连接的
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //2 第二个线程组 是用于实际的业务处理操作的
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //3 创建一个辅助类Bootstrap，就是对我们的Server进行一系列的配置
            ServerBootstrap b = new ServerBootstrap();
            //把俩个工作线程组加入进来
            b.group(bossGroup, workerGroup)
                    //我要指定使用NioServerSocketChannel这种类型的通道
                    .channel(NioServerSocketChannel.class)
                    //一定要使用 childHandler 去绑定具体的 事件处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            //将字节数组转换成Person对象和将Person对象转成字节数组,一共需要四个处理器
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(PersonModel.Person.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ServerHandler());
                        }
                    })
                    //.option(ChannelOption.SO_BACKLOG, 1024);//设置tcp缓冲区
                    //.option(ChannelOption.SO_SNDBUF, 1024*32)//设置发送缓冲区大小
                    //.option(ChannelOption.SO_RCVBUF, 1024*32)//设置接收缓冲区大小
                    //.option(ChannelOption.SO_KEEPALIVE, true);//保持连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            //绑定指定的端口 进行监听
            ChannelFuture f = b.bind(8765).sync();

            f.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Async("taskAsyncPool4Client")
    public void startConnect() {
        EventLoopGroup workgroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workgroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            //将字节数组转换成Person对象和将Person对象转成字节数组,一共需要四个处理器
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(PersonModel.Person.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture cf = b.connect("127.0.0.1", 8765).sync();

            cf.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
            workgroup.shutdownGracefully();
        }
    }

}
