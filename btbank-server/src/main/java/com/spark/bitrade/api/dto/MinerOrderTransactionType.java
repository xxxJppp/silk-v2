package com.spark.bitrade.api.dto;

/**
 * @author Administrator
 * @time 2019.10.24 14:58
 */

public enum MinerOrderTransactionType {

    //0,新订单  1,抢单，2派单，3，抢单结算，4，派单结算 ,5 订单派单超时


    NEW_ORDER(0, "新订单"),
    SECKILLED_ORDER(1, "抢到订单"),
    DISPATCHED_ORDER(2, "派出订单"),
    SECKILLED_ORDER_FINISHED(3, "抢到的订单完成"),
    DISPATCHED_ORDER_FINISHED(4, "派出的订单完成"),
    DISPATCHED_EXPRESSED_ORDER(5, "订单派单超时");


    int value;
    String name;


    MinerOrderTransactionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }
}
