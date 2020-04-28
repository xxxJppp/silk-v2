//package com.spark.bitrade.entity;
//
//import com.alibaba.fastjson.JSON;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.spark.bitrade.constant.OrderValidateStatus;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
///**
// * 机器人订单表表实体类
// * 备注：表结构保持与“exchangeOrder”一致
// *
// * @author yangch
// * @since 2019-09-02 11:23:45
// */
//@SuppressWarnings("serial")
//@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
//@ApiModel(description = "机器人订单表")
//public class ExchangeCywOrder extends ExchangeOrder {
//
//    @Override
//    public String toString() {
//        return JSON.toJSONString(this);
//    }
//
//    /**
//     * 校验状态
//     */
//    @ApiModelProperty(value = "校验状态：0 未校验， 1 校验通过， 2 校验失败", example = "")
//    private OrderValidateStatus validated;
//
////    /**
////     * 订单号，S开头的订单为星客机器人订单
////     */
////    @TableId
////    @ApiModelProperty(value = "订单号，S开头的订单为星客机器人订单", example = "")
////    private String orderId;
//
//}