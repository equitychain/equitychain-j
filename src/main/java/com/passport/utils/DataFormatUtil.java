package com.passport.utils;

import com.google.protobuf.ByteString;

import java.math.BigDecimal;

/**
 * Created by lqh on 2018/9/20.
 */
public class DataFormatUtil {
    public static BigDecimal byteAsBigDecimal(byte[] bytes) {
        return new BigDecimal(bytes == null || bytes.length == 0 ? "0" : new String(bytes));
    }
    public static String byteStringToString(ByteString byteString){
        return new String(byteString.toByteArray());
    }
}
