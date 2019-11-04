package com.chenu.pvcstumansys.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

/**
 * 基本作用：时间工具类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public class TimeUtils {

    private SimpleDateFormat simdf = new SimpleDateFormat("MM月dd日");
    private static Calendar cal = Calendar.getInstance();

    public static int getDayOfYear(){
        return cal.get(cal.YEAR);
    }

    public static int getDayOfMonth(){
        return cal.get(cal.MONTH) + 1;
    }

    public static int getDayOfDate(){
        return cal.get(cal.DATE);
    }

    /**
     * 获得格式化的日期字符串
     * @param format
     * @param date
     * @return
     */
    public static String getDateString(String format, Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前日期是星期几
     */
    public static int getDayOfWeek(){
        int[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取当前日期是星期几
     */
    public static String getDayOfWeekString(){
        String[] weekDays = { "日", "一", "二", "三", "四", "五", "六"};
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(Date date) {
        long ts = date.getTime();
        return ts;
    }


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long timeMillis, String format) {
//        long newTimeMillis = timeMillis + 28800;    //360手机系统获取的时间时区有误，得到的时间比北京时间慢八个小时，所以加上，其他手机未知有没有该bug
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    /**
     * 获得系统当前时间戳
     *
     * @return
     */
    public static long getSysCurrentTimeMillis(){
        return currentTimeMillis();
    }

}
