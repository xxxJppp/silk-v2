package com.spark.bitrade.service;

/**
 *  订单校验服务
 *
 * @author young
 * @time 2019.09.29 11:36
 */
public interface ExchangeCheckOrderService {
    /**
     * 添加校验任务
     *
     * @param orderId
     * @return
     */
    boolean addCheckTask(String orderId);

}
