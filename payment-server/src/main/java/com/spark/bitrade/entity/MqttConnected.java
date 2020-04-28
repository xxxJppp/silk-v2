package com.spark.bitrade.entity;

import lombok.Data;

/**
 * @author wsy
 * @since 2019/7/29 18:08
 */
@Data
public class MqttConnected {
    private String clientid;
    private String username;
    private String ipaddress;
    private Integer connack;
    private Long ts;
    private Integer protoVer;
    private String protoName;
    private Boolean cleanStart;
    private Integer keepalive;
}
