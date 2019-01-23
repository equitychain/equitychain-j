package com.passport.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 *
 */
public class SerializeUtils {

<<<<<<< HEAD
    /**
     * 反序列化
     *
     * @param bytes 对象对应的字节数组
     * @return
     */
    public static Object unSerialize(byte[] bytes) {
        Input input = new Input(bytes);
        Object obj = new Kryo().readClassAndObject(input);
        input.close();
        return obj;
    }

    /**
     * 序列化
     * @param object 需要序列化的对象
     * @return
     */
    public static byte[] serialize(Object object) {
        Output output = new Output(4096, -1);
        new Kryo().writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
=======

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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
