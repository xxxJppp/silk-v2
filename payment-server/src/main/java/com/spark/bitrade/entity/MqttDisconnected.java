package com.spark.bitrade.entity;

import lombok.Data;

/**
 * @author wsy
 * @since 2019/7/29 18:11
 */
@Data
public class MqttDisconnected {

    private String clientid;
    private String username;
    private String reason;
    private Long ts;

}
