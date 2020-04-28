package com.spark.bitrade.uitl;

import java.math.BigDecimal;

/**
 * 钱包工具类
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 16:09
 */
public final class CywWalletUtils {

    public static final BigDecimal ACCURACY = new BigDecimal(100000000);

    private CywWalletUtils() {
    }

    /**
     * 是否为空
     *
     * @param decimal 数值
     * @return bool
     */
    public static boolean isNone(BigDecimal decimal) {
        return decimal == null || BigDecimal.ZERO.compareTo(decimal) == 0;
    }

    /**
     * 是否是正数
     *
     * @param decimal 数值
     * @return bool
     */
    public static boolean isPositive(BigDecimal decimal) {
        return !isNone(decimal) && BigDecimal.ZERO.compareTo(decimal) < 0;
    }

    /**
     * 是否是负数
     *
     * @param decimal 数值
     * @return bool
     */
    public static boolean isNegative(BigDecimal decimal) {
        return !isNone(decimal) && BigDecimal.ZERO.compareTo(decimal) > 0;
    }

    /**
     * 是否为True
     *
     * @param bool 布尔包装器变量
     * @return bool
     */
    public static boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

    /**
     * 转换为BigDecimal
     *
     * @param doubleValue double
     * @return decimal
     */
    public static BigDecimal decimalOf(Double doubleValue) {
        if (doubleValue == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(doubleValue);
    }

    /**
     * 转换为正数
     *
     * @param decimal decimal
     * @return decimal
     */
    public static BigDecimal positiveOf(BigDecimal decimal) {
        if (decimal == null) {
            return BigDecimal.ZERO;
        }
        return decimal.abs();
    }

    /**
     * 转换为负数数
     *
     * @param decimal decimal
     * @return decimal
     */
    public static BigDecimal negativeOf(BigDecimal decimal) {
        if (decimal == null) {
            return BigDecimal.ZERO;
        }

        if (decimal.compareTo(BigDecimal.ZERO) > 0) {
            return decimal.negate();
        }
        return decimal;
    }

    /**
     * 判断两个Decimal是否相等
     *
     * @param a decimal
     * @param b decimal
     * @return bool
     */
    public static boolean decimalEqualFold(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) { // null 作无效判断
            return false;
        }
        return a.compareTo(b) == 0;
    }

    /**
     * 转换为精准数字
     *
     * @param decimal decimal
     * @return long
     */
    public static long toAccuracyValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }

        return decimal.multiply(ACCURACY).longValue();
    }

    /**
     * 转换为big decimal
     *
     * @param value long
     * @return decimal
     */
    public static BigDecimal fromAccuracyValue(long value) {
        return new BigDecimal(value)
                .divide(ACCURACY, BigDecimal.ROUND_DOWN)
                .setScale(8, BigDecimal.ROUND_DOWN);
    }
}
