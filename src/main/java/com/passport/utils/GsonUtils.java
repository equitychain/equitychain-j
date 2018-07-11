package com.passport.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * gson转换工具
 * @author 作者 xujianfeng
 * @date 创建时间：2017年3月2日 下午4:07:20
 */
public class GsonUtils {
	public final static Gson gson = new Gson();
	public final static Gson gsonBuilder = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();
	
	//对象转为json格式字符串
	public static String toJson(Object obj){
		return gson.toJson(obj);
	}
	
	//json格式字符串转为对象
	public static <T> T fromJson(Class<T> t, String json){
		return gson.fromJson(json, t);
	}
	
	//json格式字符串转为Map
	public static Map<String, Object> toMapFromJson(String json){
		return gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
	}
	
	//对象转为json格式字符串
	public static String toJsonDisableHtmlEscapin(Object obj){
		return gsonBuilder.toJson(obj);
	}
}
