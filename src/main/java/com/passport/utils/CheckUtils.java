package com.passport.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据验证工具
 * @author 作者xujianfeng 
 * @date 创建时间：2016年12月22日 下午2:14:49
 */
public class CheckUtils {
	
	/**
	 * 是否为9位数字旧汇生活码
	 * @param verifycode
	 * @return
	 */
	public static boolean isVerifycode(String verifycode){
		if(verifycode == null){
			return false;
		}
		String regex = "^[0-9]{9}$";
		return verifycode.matches(regex);
	}
	
	/**
	 * 手机号码验证
	 * @param mobile
	 * @return
	 */
	public  boolean isMobile(String mobile){
		if(StringUtils.isEmpty(mobile)){
			return false;
		}
		String regex ="^13[0-9]{9}|15[0-9]{9}|17[0-9]{9}|18[0-9]{9}|147[0-9]{8}|177[0-9]{8}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mobile);

		boolean regResult = m.matches();
		
		return regResult;
	}
	
	/**
	 * 检查是否缺少请求参数或参数为空字符串
	 * @param mapField
	 * @param params
	 * @return
	 */
	public static boolean checkParam(Map<String, Object> mapJson, String ... params) {
		boolean flag = true;
		for (String param : params) {
			if(!mapJson.containsKey(param)){
				flag = false;
				break;
			}else{
				String value = mapJson.get(param).toString();
				if(StringUtils.isEmpty(value)){
					flag = false;
					break;
				}
			}
		}
		return flag;
	}
	
	/*
     * 检查多个参数是否为null或者字符串长度为0
     */
    public static boolean checkParamIfEmpty(String ... params) {
        boolean flag = false;
        for (String param : params) {
            if(StringUtils.isEmpty(param)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
