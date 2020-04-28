package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;

import java.util.List;

/**
 * (ExchangeCywOrder)表服务接口
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
public interface ExchangeCywOrderService extends IService<ExchangeCywOrder> {

    /**
     * 查询订单
     *
     * @param memberId 会员ID
     * @param orderId  订单ID
     * @return
     */
    ExchangeCywOrder queryOrder(Long memberId, String orderId);

    /**
     * 查询历史订单
     *
     * @param page
     * @param memberId
     * @param symbol
     * @return
     */
    IPage<ExchangeOrder> historyOrders(Page page, Long memberId, String symbol);

    /**
     * 查询指定时间以内的已校验订单id
     *
     * @param time 指定时间戳
     * @return list
     */
    List<String> findOrderIdByValidatedAndLessThanTime(Long time);

    /**
     * 转移订单
     * <p>
     * 转到当月归档表中
     *
     * @param orderId 订单编号
     */
    void transfer(String orderId);
}