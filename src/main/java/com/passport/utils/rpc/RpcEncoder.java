package com.passport.utils.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC消息编码器
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
public class RpcEncoder extends MessageToByteEncoder {

  private Class<?> genericClass;

  public RpcEncoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
    if (genericClass.isInstance(in)) {
      byte[] data = SerializationUtil.serialize(in); // 序列化
      out.writeInt(data.length);
      out.writeBytes(data);
    }
  }
}