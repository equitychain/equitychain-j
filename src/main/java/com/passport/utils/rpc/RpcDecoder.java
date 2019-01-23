package com.passport.utils.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
<<<<<<< HEAD

import java.util.List;

/**
 * RPC消息解码器
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
=======
import java.util.List;

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
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
<<<<<<< HEAD
    out.add(SerializationUtil.deserialize(data, genericClass)); // 反序列化
=======
    out.add(SerializationUtil.deserialize(data, genericClass));
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  }
}
