package com.spark.bitrade.job.util;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * FuncWrapUtil
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-09 06:57
 */
public final class FuncWrapUtil {

    public static <T> T orElse(T value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static <T extends Number> boolean isNone(T value) {
        return value == null || value.intValue() == 0;
    }

    public static <T extends Number> boolean isEqual(T a, T b) {
        if (isNone(a)) {
            return isNone(b);
        }
        if (isNone(b)) {
            return isNone(a);
        }
        return a.equals(b) || a.toString().equals(b.toString());
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static boolean GreaterThan(BigDecimal a, BigDecimal b) {
        return orElse(a, BigDecimal.ZERO).compareTo(orElse(b, BigDecimal.ZERO)) > 0;
    }

    public static boolean Equal(BigDecimal a, BigDecimal b) {
        return orElse(a, BigDecimal.ZERO).compareTo(orElse(b, BigDecimal.ZERO)) == 0;
    }

    public static boolean LessThan(BigDecimal a, BigDecimal b) {
        return orElse(a, BigDecimal.ZERO).compareTo(orElse(b, BigDecimal.ZERO)) < 0;
    }

    public static boolean LessThanOrEqual(BigDecimal a, BigDecimal b) {
        return orElse(a, BigDecimal.ZERO).compareTo(orElse(b, BigDecimal.ZERO)) <= 0;
    }

    public static <T> T retryFunc(Supplier<T> func, int times) {
        try {
            while (times-- > 0) {
                T value = func.get();
                if (value != null) {
                    return value;
                }
                Thread.sleep(500);
            }
            // throw new RuntimeException("retry limit " + times);
            return null;
        } catch (InterruptedException ex) {
            throw new RuntimeException("retry thread interrupted");
        }

    }
}
