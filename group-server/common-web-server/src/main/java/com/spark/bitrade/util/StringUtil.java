package com.spark.bitrade.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * StringUtil
 *
 * @author archx
 * @since 2019/6/19 17:56
 */
public class StringUtil {

    public static <V> V to(String string, Class<V> type) {
        try {
            Constructor<V> constructor = type.getConstructor(String.class);
            return constructor.newInstance(string);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("can't convert %s to %s", string, type.getSimpleName()));
        }
    }

    public static <V> Optional<V> of(String string, Class<V> type) {
        try {
            return Optional.of(to(string, type));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
