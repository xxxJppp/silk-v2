package com.spark.bitrade.entity;

import lombok.Data;

/**
 * @author wsy
 * @since 2019/8/19 16:41
 */
@Data
public class MqttDeviceInfo {

    private String ip;
    private String mac;
    private String imei;
    private String mobile;
    private String androidId;
}
