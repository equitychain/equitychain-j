package com.passport.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.Map;


public class GsonUtils {

  public final static Gson gson = new Gson();
  public final static Gson gsonBuilder = new GsonBuilder()
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .create();


  public static String toJson(Object obj) {
    return gson.toJson(obj);
  }


  public static <T> T fromJson(Class<T> t, String json) {
    return gson.fromJson(json, t);
  }


  public static Map<String, Object> toMapFromJson(String json) {
    return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
    }.getType());
  }


  public static String toJsonDisableHtmlEscapin(Object obj) {
    return gsonBuilder.toJson(obj);
  }
}
