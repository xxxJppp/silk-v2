package com.spark.bitrade.web.bind.annotation;

import java.lang.annotation.*;

/**
 * 当前账户注解
 *
 * @author archx
 * @since 2019/5/8 17:53
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MemberAccount {

    String value() default "apiKey";
}
