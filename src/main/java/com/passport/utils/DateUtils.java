package com.passport.utils;

import com.passport.constant.Constant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * @author: xujianfeng
 * @create: 2018-09-05 11:00
 **/
public class DateUtils {
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date formatStringDate(String date){
        try {
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    private static final String WEB_URL = "http://47.75.4.251/";

    public static long getWebTime() {
        long time = Constant.GENESIS_BLOCK_TIMESTAMP;
        try {
            URL url = new URL ( WEB_URL );
            URLConnection uc = url.openConnection ( );
            uc.connect ( );
            time = uc.getDate ( );
        } catch (MalformedURLException e) {
            time = Constant.GENESIS_BLOCK_TIMESTAMP;
        } catch (IOException e) {
            time = Constant.GENESIS_BLOCK_TIMESTAMP;
        }finally {
            return time > Constant.GENESIS_BLOCK_TIMESTAMP ? time : Constant.GENESIS_BLOCK_TIMESTAMP;
        }
    }
}
