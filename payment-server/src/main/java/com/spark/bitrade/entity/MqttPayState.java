package com.spark.bitrade.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wsy
 * @since 2019/8/1 17:35
 */
@Data
public class MqttPayState {
    private Long id;                 // 订单编号
    private Long orderSn;            // 订单流水
    private Integer code;            // 设备处理状态
    private String paymentOrderNo;   // 付款流水：微信支付宝的流水号
    private String remark;           // 描述
    private Date currentTime;        // 当前时间
}
