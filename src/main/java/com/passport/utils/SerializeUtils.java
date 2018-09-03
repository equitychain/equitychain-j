package com.passport.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 *
 */
public class SerializeUtils {


  public static Object unSerialize(byte[] bytes) {
    Input input = new Input(bytes);
    Object obj = new Kryo().readClassAndObject(input);
    input.close();
    return obj;
  }

  public static byte[] serialize(Object object) {
    Output output = new Output(4096, -1);
    new Kryo().writeClassAndObject(output, object);
    byte[] bytes = output.toBytes();
    output.close();
    return bytes;
  }
}
