package com.spark.bitrade.web.valid.annotation;

import com.spark.bitrade.constant.RiskLimitEvent;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.valid.LimitValidator;

import java.lang.annotation.*;

/**
 * 限制验证注解
 *
 * @author archx
 * @since 2019/5/17 18:03
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitValid {

    /**
     * Header api key
     *
     * @return apiKey
     * @see MemberAccount#value()
     */
    String value() default "apiKey";

    /**
     * 指定限制类型
     *
     * @return execute_limit
     */
    RiskLimitEvent limit() default RiskLimitEvent.NONE;

    /**
     * 指定验证器实现
     * <p>
     * 多个验证器将逐一验证
     *
     * @return []
     */
    Class<? extends LimitValidator>[] validBy() default {};
}
