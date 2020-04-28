package com.spark.bitrade.service;

import java.util.Set;

/**
 *  订单校验服务
 *
 * @author young
 * @time 2019.09.29 11:36
 */
public interface CywCheckOrderService {
    /**
     * 添加校验任务
     *
     * @param orderId
     * @return
     */
    boolean addCheckTask(String orderId);

}
