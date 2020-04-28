package com.spark.bitrade.job.util;

import com.spark.bitrade.trans.Tuple2;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtils
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/25 17:21
 */
public final class DateUtils {

    /**
     * 获取前一天的首尾时间
     *
     * @return tuple2
     */
    public static Tuple2<Date, Date> getHeadAndTailOfYesterday() {
        Calendar instance = Calendar.getInstance();

        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);


        Date tail = fromTimestamp(instance.getTimeInMillis() - 1);

        instance.add(Calendar.DATE, -1);
        Date head = instance.getTime();

        return new Tuple2<>(head, tail);
    }

    /**
     * 字符串转日期
     *
     * @param dateRangeStr 日期范围字符串
     * @param separator    分隔符
     * @param pattern      日期格式 eg. yyyy-MM-dd HH:mm:ss
     * @return tuple2
     */
    public static Tuple2<Date, Date> parseDateRange(String dateRangeStr, String separator, String pattern) {
        if (!StringUtils.hasText(dateRangeStr)) {
            return null;
        }
        String[] strings = dateRangeStr.split(separator);
        if (strings.length != 2) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(pattern);

        try {
            Date first = df.parse(strings[0]);
            Date second = df.parse(strings[1]);
            return new Tuple2<>(first, second);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一天的首位日期
     * <p>
     * 如果入参为null,则为当天
     *
     * @param date date
     * @return tuple2
     */
    public static Tuple2<Date, Date> getHeadTailOf(Date date) {
        Calendar instance = Calendar.getInstance();

        if (date != null) {
            instance.setTime(date);
        }

        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        Date head = instance.getTime();

        instance.add(Calendar.DATE, 1);
        Date tail = fromTimestamp(instance.getTimeInMillis() - 1);

        return new Tuple2<>(head, tail);
    }

    public static Date fromTimestamp(long timestamp) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timestamp);
        return instance.getTime();
    }
}
