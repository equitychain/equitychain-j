package com.passport.utils.rpc;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;


public class SerializationUtil {

  private static final Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

  private static final Objenesis objenesis = new ObjenesisStd(true);


  @SuppressWarnings("unchecked")
  public static <T> byte[] serialize(T obj) {
    Class<T> cls = (Class<T>) obj.getClass();
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    try {
      Schema<T> schema = getSchema(cls);
      return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    } finally {
      buffer.clear();
    }
  }

  public static <T> T deserialize(byte[] data, Class<T> cls) {
    try {
      T message = objenesis.newInstance(cls);
      Schema<T> schema = getSchema(cls);
      ProtostuffIOUtil.mergeFrom(data, message, schema);
      return message;
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> Schema<T> getSchema(Class<T> cls) {
    Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
    if (schema == null) {
      schema = RuntimeSchema.createFrom(cls);
      cachedSchema.put(cls, schema);
    }
    return schema;
  }
}
