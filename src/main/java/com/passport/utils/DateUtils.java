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
    // 默认日期格式
    public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd";

    // 默认时间格式
    public static final String DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATETIME_HOURS_FORMAT = "yyyy-MM-dd HH:00:00";

    public static final String DATETIME_HOUR = "yyyy年MM月dd日 HH时";

    public static final String TIME_DEFAULT_FORMAT = "HH:mm:ss";

    // 日期格式化
    private static DateFormat dateFormat = new SimpleDateFormat ( DATE_DEFAULT_FORMAT );

    // 时间格式化
    private static DateFormat dateTimeFormat = new SimpleDateFormat ( DATETIME_DEFAULT_FORMAT );

    private static DateFormat dateTimeHoursFormat = new SimpleDateFormat ( DATETIME_HOURS_FORMAT);

    private static DateFormat dateTimeHour = new SimpleDateFormat ( DATETIME_HOUR );

    private static DateFormat timeFormat =  new SimpleDateFormat ( TIME_DEFAULT_FORMAT );

    private static Calendar gregorianCalendar = new GregorianCalendar ( );

    private static final String WEB_URL = "http://47.90.81.166/";


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(Long s) {
        String res;
        long lt = new Long ( s );
        Date date = new Date ( lt );
        res = dateTimeFormat.format ( date );
        return res;
    }
    public static String stampToDateHours(Long s) {
        String res;
        long lt = new Long ( s );
        Date date = new Date ( lt );
        res = dateTimeHoursFormat.format ( date );
        return res;
    }

    public static String stampToDateOfHour(Long s) {
        String res;
        long lt = new Long ( s );
        Date date = new Date ( lt );
        res = dateTimeHour.format ( date );
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        Date date = dateTimeFormat.parse ( s );
        long ts = date.getTime ( );
        res = String.valueOf ( ts );
        return res;
    }


    /**
     * 日期格式化yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static Date formatDate(String date, String format) {
        try {
            return new SimpleDateFormat ( format ).parse ( date );
        } catch (ParseException e) {
            e.printStackTrace ( );
        }
        return null;
    }

    /**
     * 日期格式化yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDateFormat(Date date) {
        return dateFormat.format ( date );
    }

    /**
     * 日期格式化yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDateTimeFormat(Date date) {
        return dateTimeFormat.format ( date );
    }

    /**
     * 时间格式化
     *
     * @param date
     * @return HH:mm:ss
     */
    public static String getTimeFormat(Date date) {
        return timeFormat.format ( date );
    }


    /**
     * 日期格式化
     *
     * @param date
     * @return
     */
    public static Date getDateFormat(String date) {
        try {
            return dateFormat.parse ( date );
        } catch (ParseException e) {
            e.printStackTrace ( );
        }
        return null;
    }

    /**
     * 时间格式化
     *
     * @param date
     * @return
     */
    public static Date getDateTimeFormat(String date) {
        try {
            return dateTimeFormat.parse ( date );
        } catch (ParseException e) {
            e.printStackTrace ( );
        }
        return null;
    }

    /**
     * 获取当前日期(yyyy-MM-dd)
     *
     * @return
     */
    public static Date getNowDate() {
        return getDateFormat ( dateFormat.format ( new Date ( ) ) );
    }

    /**
     * 获取当前日期星期一日期
     *
     * @return date
     */
    public static Date getFirstDayOfWeek() {
        gregorianCalendar.setFirstDayOfWeek ( Calendar.MONDAY );
        gregorianCalendar.setTime ( new Date ( ) );
        gregorianCalendar.set ( Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek ( ) ); // Monday
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取当前日期星期日日期
     *
     * @return date
     */
    public static Date getLastDayOfWeek() {
        gregorianCalendar.setFirstDayOfWeek ( Calendar.MONDAY );
        gregorianCalendar.setTime ( new Date ( ) );
        gregorianCalendar.set ( Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek ( ) + 6 ); // Monday
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取日期星期一日期
     *
     * @return date
     */
    public static Date getFirstDayOfWeek(Date date) {
        if ( date == null ) {
            return null;
        }
        gregorianCalendar.setFirstDayOfWeek ( Calendar.MONDAY );
        gregorianCalendar.setTime ( date );
        gregorianCalendar.set ( Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek ( ) ); // Monday
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取日期星期一日期
     *
     * @return date
     */
    public static Date getLastDayOfWeek(Date date) {
        if ( date == null ) {
            return null;
        }
        gregorianCalendar.setFirstDayOfWeek ( Calendar.MONDAY );
        gregorianCalendar.setTime ( date );
        gregorianCalendar.set ( Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek ( ) + 6 ); // Monday
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取当前月的第一天
     *
     * @return date
     */
    public static Date getFirstDayOfMonth() {
        gregorianCalendar.setTime ( new Date ( ) );
        gregorianCalendar.set ( Calendar.DAY_OF_MONTH, 1 );
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取当前月的最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        gregorianCalendar.setTime ( new Date ( ) );
        gregorianCalendar.set ( Calendar.DAY_OF_MONTH, 1 );
        gregorianCalendar.add ( Calendar.MONTH, 1 );
        gregorianCalendar.add ( Calendar.DAY_OF_MONTH, -1 );
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取指定月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        gregorianCalendar.setTime ( date );
        gregorianCalendar.set ( Calendar.DAY_OF_MONTH, 1 );
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取指定月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        gregorianCalendar.setTime ( date );
        gregorianCalendar.set ( Calendar.DAY_OF_MONTH, 1 );
        gregorianCalendar.add ( Calendar.MONTH, 1 );
        gregorianCalendar.add ( Calendar.DAY_OF_MONTH, -1 );
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取日期前一天
     *
     * @param date
     * @return
     */
    public static Date getDayBefore(Date date) {
        gregorianCalendar.setTime ( date );
        int day = gregorianCalendar.get ( Calendar.DATE );
        gregorianCalendar.set ( Calendar.DATE, day - 1 );
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取日期后一天
     *
     * @param date
     * @return
     */
    public static Date getDayAfter(Date date) {
        gregorianCalendar.setTime ( date );
        int day = gregorianCalendar.get ( Calendar.DATE );
        gregorianCalendar.set ( Calendar.DATE, day + 1 );
        return gregorianCalendar.getTime ( );
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static int getNowYear() {
        Calendar d = Calendar.getInstance ( );
        return d.get ( Calendar.YEAR );
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getNowMonth() {
        Calendar d = Calendar.getInstance ( );
        return d.get ( Calendar.MONTH ) + 1;
    }

    /**
     * 获取当月天数
     *
     * @return
     */
    public static int getNowMonthDay() {
        Calendar d = Calendar.getInstance ( );
        return d.getActualMaximum ( Calendar.DATE );
    }

    /**
     * 获取时间段的每一天
     *
     * @param startDate 开始日期
     * @param endDate   结算日期
     * @return 日期列表
     */
    public static List<Date> getEveryDay(Date startDate, Date endDate) {
        if ( startDate == null || endDate == null ) {
            return null;
        }
        // 格式化日期(yy-MM-dd)
        startDate = getDateFormat ( getDateFormat ( startDate ) );
        endDate = getDateFormat ( getDateFormat ( endDate ) );
        List<Date> dates = new ArrayList<Date> ( );
        gregorianCalendar.setTime ( startDate );
        dates.add ( gregorianCalendar.getTime ( ) );
        while (gregorianCalendar.getTime ( ).compareTo ( endDate ) < 0) {
            // 加1天
            gregorianCalendar.add ( Calendar.DAY_OF_MONTH, 1 );
            dates.add ( gregorianCalendar.getTime ( ) );
        }
        return dates;
    }

    /**
     * 获取提前多少个月
     *
     * @param monty
     * @return
     */
    public static Date getFirstMonth(int monty) {
        Calendar c = Calendar.getInstance ( );
        c.add ( Calendar.MONTH, -monty );
        return c.getTime ( );
    }


    public static long getWebTime() {
        long time = Constant.GENESIS_BLOCK_TIMESTAMP;
        try {
            URL url = new URL ( WEB_URL );
            URLConnection uc = url.openConnection ( );
            uc.connect ( );
            time = uc.getDate ( );
        } catch (MalformedURLException e) {
//            e.printStackTrace ( );
            time = System.currentTimeMillis();
        } catch (IOException e) {
//            e.printStackTrace ( );
            time = System.currentTimeMillis();
        }finally {
            return time > Constant.GENESIS_BLOCK_TIMESTAMP ? time : Constant.GENESIS_BLOCK_TIMESTAMP;
        }
    }

    public static void main(String[] args) {
        String s = getWebTime()+"";
        System.out.println(s);
        System.out.println(stampToDate(s));

    }
}
