package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 *  订单服务接口
 *
 * @author young
 * @time 2019.09.02 11:46
 */
public interface CywOrderService {

    /**
     * 下单委托
     *
     * @param exchangeCywOrder
     * @return
     */
    ExchangeOrder createOrder(ExchangeCywOrder exchangeCywOrder);

    //成交明细

    /**
     * 成交订单
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 交易数量
     * @param turnover     交易额
     * @return
     */
    ExchangeOrder completedOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover);

    /**
     * 查询订单信息
     *
     * @param memberId 用户ID
     * @param orderId  订单号，格式=S雪花流水ID_交易对
     * @return
     */
    ExchangeOrder queryOrder(Long memberId, String orderId);

    /**
     * 撤销订单申请
     *
     * @param memberId 用户ID
     * @param orderId  订单号，格式=S雪花流水ID_交易对
     * @return
     */
    ExchangeOrder claimCancelOrder(Long memberId, String orderId);

    /**
     * 撤销订单（撮合器中存在的订单）
     * 备注：仅更改订单状态
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 交易数量，可以为0
     * @param turnover     交易额，可以为0
     * @return
     */
    ExchangeOrder canceledOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover);

    /**
     * 撤销订单（撮合器中不存在的订单）
     * 备注：仅更改订单状态
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    ExchangeOrder canceledOrder(Long memberId, String orderId);

    /**
     * 入库撤销订单
     *
     * @param orderId 订单号
     */
    void canceledOrder(String orderId);

    /**
     * 正在交易的用户
     *
     * @param symbol 交易对
     * @return
     */
    Set<Long> openMembers(String symbol);

    /**
     * 交易中的订单
     *
     * @param symbol   交易对
     * @param memberId
     */
    List<ExchangeOrder> openOrders(String symbol, Long memberId);


    /**
     * 历史订单查询
     *
     * @param symbol   交易对
     * @param memberId
     * @param size     分页.每页数量
     * @param current  分页.当前页码
     */
    IPage<ExchangeOrder> historyOrders(String symbol, Long memberId, Integer size, Integer current);

    /**
     * 获取撮合明细
     *
     * @param orderId 订单号
     * @return 撮合明细列表
     */
    List<ExchangeOrderDetail> listTradeDetail(String orderId);
}
