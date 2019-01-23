package com.passport.utils;

<<<<<<< HEAD
import com.passport.core.Transaction;
import com.passport.crypto.eth.Keys;
import com.passport.crypto.eth.Sign;
import com.passport.enums.TransactionTypeEnum;
import com.passport.transactionhandler.TransactionStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据验证工具
 * @author 作者xujianfeng 
 * @date 创建时间：2016年12月22日 下午2:14:49
 */
public class CheckUtils {
	private static final Logger logger = LoggerFactory.getLogger(CheckUtils.class);
	
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

	/**
	 * 交易数据验签
	 * @param transaction
	 * @return
	 */
	public static boolean checkTransaction(Transaction transaction){
		//验证签名
		Transaction trans = new Transaction();
		trans.setPayAddress(transaction.getPayAddress());
		trans.setReceiptAddress(transaction.getReceiptAddress());
		trans.setValue(transaction.getValue());
		trans.setExtarData(transaction.getExtarData());
		trans.setTime(transaction.getTime());
		trans.setToken(transaction.getToken());
		//生成hash和生成签名sign使用的基础数据都应该一样 TODO 使用多语言开发时应使用同样的序列化算法
		String transactionJson = GsonUtils.toJson(trans);
		try {
			if(transaction.getPayAddress() == null && transaction.getTradeType().equals(TransactionTypeEnum.BLOCK_REWARD.toString())){
				if (!Sign.verify(Keys.publicKeyDecode(new String(transaction.getPublicKey())), new String(transaction.getSignature()), transactionJson)) {
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("交易流水验签异常", e);
			return false;
		}
		return true;
	}
=======
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;


public class CheckUtils {


  public static boolean isVerifycode(String verifycode) {
    if (verifycode == null) {
      return false;
    }
    String regex = "^[0-9]{9}$";
    return verifycode.matches(regex);
  }

  public static boolean checkParam(Map<String, Object> mapJson, String... params) {
    boolean flag = true;
    for (String param : params) {
      if (!mapJson.containsKey(param)) {
        flag = false;
        break;
      } else {
        String value = mapJson.get(param).toString();
        if (StringUtils.isEmpty(value)) {
          flag = false;
          break;
        }
      }
    }
    return flag;
  }

  public static boolean checkParamIfEmpty(String... params) {
    boolean flag = false;
    for (String param : params) {
      if (StringUtils.isEmpty(param)) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  public boolean isMobile(String mobile) {
    if (StringUtils.isEmpty(mobile)) {
      return false;
    }
    String regex = "^13[0-9]{9}|15[0-9]{9}|17[0-9]{9}|18[0-9]{9}|147[0-9]{8}|177[0-9]{8}$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(mobile);

    boolean regResult = m.matches();

    return regResult;
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
