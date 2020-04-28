package com.spark.bitrade.utils;

import com.spark.bitrade.constant.MemberLevelTypeEnum;
import com.spark.bitrade.constant.MemberMsgCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.BenefitsOrderForm;
import com.spark.bitrade.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 09:28  
 */
@Slf4j
@Component
public class MemberUtil {

    /**
     * 验证资金密码
     *
     * @param moneyPassword 前端输入的资金密码
     * @param jyPassword    用户设置的资金密码
     */
    public static void validatePassword(String moneyPassword, String jyPassword, String salt) {
        AssertUtil.hasText(moneyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", moneyPassword, salt, 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPassword.equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
    }

    /**
     * 校验开通会员表单
     */
    public static void validataOpenVipForm(BenefitsOrderForm form) {
        if (form.getDuration() == 0) {
            throw new MessageCodeException(MemberMsgCode.OPEN_DAYS_NOT_ZREO);
        }
    }

    /**
     * 判断能否被30整除
     *
     * @param days 天数
     * @return
     */
    public static Boolean checkDays(Integer days, Integer rule) {
        if (days % rule == 0) {
            return true;
        } else {
            throw new MessageCodeException(MemberMsgCode.DAYS_IS_NOTMULTIPLE_OF_THIRTY);
        }
    }

    /**
     * 指定日期加上天数后的日期
     *
     * @param num     为增加的天数
     * @param newDate 创建时间
     * @return
     * @throws ParseException
     */
    public static Date plusDay(Integer num, Date newDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Calendar ca = Calendar.getInstance();
        ca.setTime(newDate);
        // num为增加的天数，可以改变的
        ca.add(Calendar.DATE, num);
        newDate = ca.getTime();
        String enddate = format.format(newDate);
        Date strtodate = format.parse(enddate, pos);
        return strtodate;
    }

    /**
     * 计算两个时间差
     *
     * @param endTime   结束时间
     * @param startTime 开始时间
     */
    public static Integer timeDifference(Date endTime, Date startTime) throws ParseException {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleFormat.format(startTime);
        /*天数差*/
        long from1 = startTime.getTime();
        long to1 = endTime.getTime();
        int days = (int) ((to1 - from1) / (1000 * 60 * 60 * 24));
        return days;
    }

    /**
     * 计算两个时间差
     *
     * @param smdate 开始日期
     * @param bdate  结束日期
     * @return
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));

    }

    /**
     * Vip 转换名字在
     *
     * @param code
     * @return
     */
    public static String getValueByCode(Integer code) {
        for (MemberLevelTypeEnum levelTypeEnum : MemberLevelTypeEnum.values()) {
            if (code == levelTypeEnum.getCode()) {
                return levelTypeEnum.getName();
            }
        }
        return null;
    }
}
