package com.spark.bitrade.mqtt.annotations;

import java.lang.annotation.*;

/**
 * @author wsy
 * @since 2019/7/19 14:23
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttPayload {

}
