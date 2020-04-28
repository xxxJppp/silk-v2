package com.spark.bitrade.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	
	public static void main(String[] args) throws ParseException {
		TimeUtil t = new TimeUtil();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date t1 = sdf.parse("2019-12-16 00:23:09");
		Date t2 = sdf.parse("2019-12-16 18:23:50");
		
		long toTime = TimeUtil.getCurrentHourTime(t1).getTime(),fromTime = TimeUtil.getHourTime(t1, 2, "-").getTime();
		
		System.out.println(sdf.format(new Date(fromTime)) + "==========" + sdf.format(new Date(toTime)));
		
		System.out.println(fromTime + "---" + toTime);
	}

	/**
     * 获取当前时间小时整点时间
     *
     * @param 
     * @return
     */
    public static Date getCurrentHourTime(Date date) {
        return getHourTime(date, 0, "=");
    }
 
    /**
     * 获取指定时间上n个小时整点时间
     *
     * @param date
     * @return
     */
    public static Date getLastHourTime(Date date, int n) {
        return getHourTime(date, n, "-");
    }
 
    /**
     * 获取指定时间下n个小时整点时间
     *
     * @param date
     * @return
     */
    public static Date getNextHourTime(Date date, int n) {
        return getHourTime(date, n, "+");
    }
 
    /**
     * 获取指定时间n个小时整点时间
     *
     * @param date
     * @return
     */
    public static Date getHourTime(Date date, int n, String direction) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        switch (direction) {
            case "+":
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) + n);
                break;
            case "-":
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - n);
                break;
            case "=":
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY));
                break;
            default:
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY));
        }
 
        date = ca.getTime();
        return date;
    }

}
