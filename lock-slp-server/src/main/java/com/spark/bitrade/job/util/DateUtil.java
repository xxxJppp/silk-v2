package com.spark.bitrade.job.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtil
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-08 21:20
 */
public final class DateUtil {

    static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateUtil() {
    }

    /**
     * 获取当期系统时间
     *
     * @return date
     */
    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取指定日期的前一天
     *
     * @param date 日期
     * @return date
     */
    public static Date getTheDayBeforeOf(Date date) {
        Calendar instance = convert(date);
        instance.add(Calendar.DATE, -1);

        return instance.getTime();
    }

    /**
     * 获取指定日期的后一天
     *
     * @param date 日期
     * @return date
     */
    public static Date getTheDayAfter(Date date) {
        Calendar instance = convert(date);
        instance.add(Calendar.DATE, 1);

        return instance.getTime();
    }

    /**
     * 转换时间
     *
     * @param datetime 日期字符串
     * @return date
     */
    public static Date parse(String datetime) {
        try {
            return new SimpleDateFormat(PATTERN).parse(datetime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid datetime");
        }
    }

    /**
     * 格式化时间
     *
     * @param date 时间
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String format(Date date) {
        return new SimpleDateFormat(PATTERN).format(date);
    }

    private static Calendar convert(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static void main(String[] args) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2019-08-01");
        Date theDayBeforeOf = getTheDayBeforeOf(date);
        System.out.println(format(theDayBeforeOf));

    }
}
