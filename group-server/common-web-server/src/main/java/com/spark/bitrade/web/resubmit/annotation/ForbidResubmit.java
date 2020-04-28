package com.spark.bitrade.web.resubmit.annotation;

import java.lang.annotation.*;

/**
 *  禁止重复提交
 *
 * @author young
 * @time 2019.07.18 19:04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForbidResubmit {
    /**
     *是否开启验证功能
     * @return
     */
    boolean value() default true;

    /**
     * 阻断多长时间（单位：秒）
     * @return
     */
    long interdictTime() default 5;
}
