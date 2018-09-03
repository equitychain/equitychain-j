package com.passport.utils;

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
}
