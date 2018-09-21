package com.passport.utils;

import java.math.BigDecimal;

/**
 * Created by lqh on 2018/9/20.
 */
public class DataFormatUtil {
    public static BigDecimal byteAsBigDecimal(byte[] bytes) {
        return new BigDecimal(bytes == null || bytes.length == 0 ? "0" : new String(bytes));
    }
}
