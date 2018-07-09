package com.passport.utils.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * RPC消息解码器
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
public class RpcDecoder extends ByteToMessageDecoder {

  private Class<?> genericClass;

  public RpcDecoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() < 4) {
      return;
    }
    in.markReaderIndex();
    int dataLength = in.readInt();
    if (in.readableBytes() < dataLength) {
      in.resetReaderIndex();
      return;
    }
    byte[] data = new byte[dataLength];
    in.readBytes(data);
    out.add(SerializationUtil.deserialize(data, genericClass)); // 反序列化
  }
}
