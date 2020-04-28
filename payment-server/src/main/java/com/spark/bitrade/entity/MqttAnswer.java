package com.spark.bitrade.entity;

import com.spark.bitrade.emums.MqttAnswerType;
import lombok.Data;

import java.util.Date;

/**
 * @author wsy
 * @since 2019/8/16 9:42
 */
@Data
public class MqttAnswer {

    private Date currentTime;             // 当前时间
    private Long id;                      // 订单编号
    private Long orderSn;                 // 订单流水
    private MqttAnswerType type;          // 应答类型

}
