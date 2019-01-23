package com.passport.utils.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

<<<<<<< HEAD
/**
 * RPC消息编码器
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
public class RpcEncoder extends MessageToByteEncoder {

  private Class<?> genericClass;

  public RpcEncoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
    if (genericClass.isInstance(in)) {
<<<<<<< HEAD
      byte[] data = SerializationUtil.serialize(in); // 序列化
=======
      byte[] data = SerializationUtil.serialize(in);
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      out.writeInt(data.length);
      out.writeBytes(data);
    }
  }
}