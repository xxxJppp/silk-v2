package com.spark.bitrade.service;

/**
 * ExchangeOrderValidator
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/29 16:14
 */
public interface ExchangeOrderValidator {

    /**
     * 验证订单
     *
     * @param orderId 订单id
     */
    void validate(String orderId);

    /**
     * 校验订单
     *
     * @param orderId
     * @return 错误信息，为null时为校验成功
     */
    String redo(String orderId);
}
