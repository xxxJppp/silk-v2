package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeReleaseReferrerOrder;

/**
 * 币币交易-推荐人闪兑订单表(ExchangeReleaseReferrerOrder)表服务接口
 *
 * @author yangch
 * @since 2020-01-17 17:18:13
 */
public interface ExchangeReleaseReferrerOrderService extends IService<ExchangeReleaseReferrerOrder> {
    /**
     * 从主库查询
     *
     * @param id
     * @return
     */
    ExchangeReleaseReferrerOrder findOne(String id);

    /**
     * 闪兑前的准备
     *
     * @param order
     */
    void preExchange(ExchangeOrder order);

    /**
     * 完成ESP闪兑兑换
     *
     * @param orderId 订单ID
     */
    void exchange(String orderId);

}