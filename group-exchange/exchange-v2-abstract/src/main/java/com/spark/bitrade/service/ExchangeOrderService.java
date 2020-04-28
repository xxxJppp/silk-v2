package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.dto.ExchangeOrderDto;
import com.spark.bitrade.dto.ExchangeOrderStats;
import com.spark.bitrade.entity.ExchangeOrder;

import java.util.List;

/**
 * 币币订单表服务接口
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
public interface ExchangeOrderService extends IService<ExchangeOrder> {

    /**
     * 创建订单
     *
     * @param exchangeOrder
     * @return
     */
    ExchangeOrder createOrder(ExchangeOrder exchangeOrder);

    /**
     * 查询订单
     *
     * @param memberId 会员ID
     * @param orderId  订单ID
     * @return
     */
    ExchangeOrder queryOrder(Long memberId, String orderId);

    /**
     * 查询订单(从主库查询，不存在延迟)
     *
     * @param memberId 会员ID
     * @param orderId  订单ID
     * @return
     */
    ExchangeOrder queryOrderWithMaster(Long memberId, String orderId);

    /**
     * 查询订单（缓存订单）
     *
     * @param memberId
     * @param orderId
     * @return
     */
    ExchangeOrder queryOrderWithCache(Long memberId, String orderId);

    /**
     * 根据指定的订单号和状态修改记录
     *
     * @param order     订单信息
     * @param oldStatus 修改前的订单状态
     * @return
     */
    boolean updateByOrderIdAndStatus(ExchangeOrder order, ExchangeOrderStatus oldStatus);


    /**
     * 查询订单
     *
     * @param page
     * @param uid        用户ID
     * @param symbol     交易对
     * @param coinSymbol 交易币
     * @param baseSymbol 基币
     * @param direction  方向
     * @return
     */
    IPage<ExchangeOrderDto> openOrders(Page page, Long uid,
                                       String symbol,
                                       String coinSymbol,
                                       String baseSymbol,
                                       ExchangeOrderDirection direction);

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
     * 查询历史订单
     *
     * @param page
     * @param uid        用户ID
     * @param symbol     交易对
     * @param coinSymbol 交易币
     * @param baseSymbol 基币
     * @param direction  方向
     * @param status     订单状态
     * @param startTime  开始时间戳（包含）
     * @param endTime    截止时间戳
     * @return
     */
    IPage<ExchangeOrderDto> historyOrders(Page page, Long uid,
                                          String symbol,
                                          String coinSymbol,
                                          String baseSymbol,
                                          ExchangeOrderDirection direction,
                                          ExchangeOrderStatus status,
                                          Long startTime,
                                          Long endTime);

    /**
     * 查询交易中的数量
     *
     * @param memberId  会员ID
     * @param symbol    交易对
     * @param direction 交易方向
     * @return
     */
    int findCurrentTradingCount(Long memberId, String symbol, ExchangeOrderDirection direction);

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

    /**
     * 项目方中心 数据统计
     *
     * @param coinSymbol 项目方币种
     * @param coinSymbol 项目方币种
     * @param status     订单状态，不传递为所有订单，0=交易中，1=已完成订单
     * @param startTime  统计开始时间
     * @param endTime    统计截止时间
     * @return
     */
    List<ExchangeOrderStats> stats(ExchangeOrderType type, String coinSymbol, Integer status, Long startTime, Long endTime);
}