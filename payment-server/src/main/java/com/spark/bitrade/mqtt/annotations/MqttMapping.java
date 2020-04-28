package com.spark.bitrade.mqtt.annotations;

import java.lang.annotation.*;

/**
 * @author wsy
 * @since 2019/7/18 17:34
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMapping {

    String[] value() default {};

}
