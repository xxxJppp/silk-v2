package com.spark.bitrade.constants;


/**
 * @author shenzucai
 * @time 2019.08.05 13:54
 */
public enum  CommandCode  {

    /**
     * 订单分发
     */
    ORDER_DISTRIBUTE(0, "ORDER_DISTRIBUTE"),
    /**
     * 订单分发成功，由此将更新匹配记录和订单记录的付款状态为付款中
     */
    ORDER_DISTRIBUTE_SUCCESS(1, "ORDER_DISTRIBUTE_SUCCESS"),
    /**
     * 订单付款成功
     */
    ORDER_PAY_SUCCESS(2, "ORDER_PAY_SUCCESS"),
    /**
     * 订单付款失败
     */
    ORDER_PAY_FAILED(3, "ORDER_PAY_FAILED"),
    /**
     * 订单匹配成功
     */
    ORDER_MATCH_SUCCESS(4, "ORDER_MATCH_SUCCESS"),
    /**
     * 订单匹配失败
     */
    ORDER_MATCH_FAILED(5, "ORDER_MATCH_FAILED"),

    /**
     * 订单付款成功应答
     */
    ANSWER_ORDER_PAY_SUCCESS(6, "ANSWER_ORDER_PAY_SUCCESS"),
    ;

    private final int    code;
    private final String message;

    CommandCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
