package com.spark.bitrade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wsy
 * @since 2019/8/1 17:18
 */
@Data
public class MqttPayTask {

    private Long id;                 // 订单编号
    private Long orderSn;            // 订单流水
    private Integer paymentType;     // 付款方式
    private BigDecimal paymentMoney; // 付款金额
    private String paymentPassword;  // 支付密码
    private String receiptName;      // 收款人姓名
    private String receiptQrCode;    // 收款二维码
    private String paymentNote;      // 付款备注
    private Date createTime;         // 创建时间
    private Date currentTime;        // 当前时间
}
