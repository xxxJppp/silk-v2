package com.spark.bitrade.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * SlpReleaseJobParam
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 11:05
 */
public class SlpReleaseJobParam {

    static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private String datetime;
    private long limit;

    private Calendar calendar;


    private SlpReleaseJobParam() {
        this.calendar = Calendar.getInstance();
        this.datetime = new SimpleDateFormat(PATTERN).format(calendar.getTime());
        this.init();
    }

    private SlpReleaseJobParam(String datetime) {
        this.datetime = datetime;
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(getDate());
        this.init();
    }

    private void init() {
        this.calendar.add(Calendar.DATE, -1);
        this.limit = calendar.getTimeInMillis();
    }

    private Date getDate() {
        try {
            return new SimpleDateFormat(PATTERN).parse(datetime);
        } catch (ParseException e) {
            throw new RuntimeException("invalid datetime " + datetime);
        }
    }

    /**
     * 获取日期时间
     *
     * @return datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * 获取限制时间戳
     *
     * @return limit
     */
    public long getLimit() {
        return limit;
    }

    /**
     * 获取限制时间
     *
     * @return datetime
     */
    public String getLimitDatetime() {
        return new SimpleDateFormat(PATTERN).format(calendar.getTime());
    }

    /**
     * 判断目标日期是否释放过
     *
     * @param releasedAt 上一次释放日期
     * @return bool
     */
    public boolean isReleased(Date releasedAt) {
        return releasedAt.getTime() > limit;
    }

    public static SlpReleaseJobParam of(String param) {
        if (!StringUtils.hasText(param)) {
            param = "[]";
        }

        // 转为数组
        JSONArray array = JSON.parseArray(param);

        if (array.size() == 1) {
            String string = array.getString(0);
            return new SlpReleaseJobParam(string);
        }
        return new SlpReleaseJobParam();
    }

    private static String format(Date date) {
       return new SimpleDateFormat(PATTERN).format(date);
    }

    public static void main(String[] args) throws ParseException {
        SlpReleaseJobParam of = of("[\"2019-07-23 22:21:27\"]");
        System.out.println(of.getDatetime());

        long limit = of.getLimit();
        Calendar in = Calendar.getInstance();
        in.setTimeInMillis(limit);
        System.out.println(format(in.getTime()));

        Date date = new SimpleDateFormat(PATTERN).parse("2019-07-22 22:21:29");

        System.out.println(of.isReleased(date));

    }
}
