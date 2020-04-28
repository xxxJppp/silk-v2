package com.spark.bitrade.mqtt.annotations;

import java.lang.annotation.*;

/**
 * @author wsy
 * @since 2019/7/18 17:54
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttVariable {

    String value();

}
