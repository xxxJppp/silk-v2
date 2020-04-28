package com.spark.bitrade.util;

import com.spark.bitrade.entity.SilkDataDist;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * SilkDataDistUtils
 *
 * @author archx
 * @since 2019/6/3 14:21
 */
public abstract class SilkDataDistUtils {

    public static <T> T getVal(SilkDataDist dist, Class<T> clazz, T defaultValue) {

        if (dist != null && clazz.getName().equals(dist.getDictType().replace("_","."))) {
            try {
                Constructor<T> constructor = clazz.getConstructor(String.class);
                return constructor.newInstance(dist.getDictVal());
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
                // ignored
            }
        }
        return defaultValue;
    }

    public static void main(String[] args) {
        SilkDataDist dist = new SilkDataDist();
        //dist.setDictType("java.lang.Integer");
        dist.setDictType("java_lang_Integer");
        dist.setDictVal("30");

        Integer val = getVal(dist, Integer.class, 0);
        Assert.isTrue(val == 30, "验证失败");
    }
}
