package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.dto.ExchangeOrderDto;
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
public interface OrderFacadeService {

    /**
     * 下单委托
     *
     * @param exchangeOrder
     * @return
     */
    ExchangeOrder createOrder(ExchangeOrder exchangeOrder);

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
     * @param orderId  订单号，格式=E雪花流水ID
     * @return
     */
    ExchangeOrder queryOrder(Long memberId, String orderId);

    /**
     * 查询订单信息及撮合明细
     *
     * @param memberId 用户ID
     * @param orderId  订单号，格式=E雪花流水ID
     * @return
     */
    ExchangeOrderDto queryOrderDetail(Long memberId, String orderId);

    /**
     * 撤销订单申请
     *
     * @param memberId 用户ID
     * @param orderId  订单号，格式=E雪花流水ID
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
     * 交易中的订单
     *
     * @param symbol   交易对
     * @param memberId
     */
    List<ExchangeOrder> openOrders(String symbol, Long memberId);

    /**
     * 查询订单
     *
     * @param uid        用户ID
     * @param symbol     交易对
     * @param pageNo
     * @param pageSize
     * @param coinSymbol 交易币
     * @param baseSymbol 基币
     * @param direction  方向
     * @return
     */
    IPage<ExchangeOrderDto> openOrders(Long uid, String symbol,
                                       int pageNo, int pageSize,
                                       String coinSymbol,
                                       String baseSymbol,
                                       ExchangeOrderDirection direction);


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
     * 历史订单查询
     *
     * @param uid        用户ID
     * @param symbol     交易对
     * @param pageNo
     * @param pageSize
     * @param coinSymbol 交易币
     * @param baseSymbol 基币
     * @param direction  方向
     * @param status
     * @param startTime  开始时间戳（包含）
     * @param endTime    截止时间戳
     * @return
     */
    IPage<ExchangeOrderDto> historyOrders(Long uid, String symbol,
                                          int pageNo, int pageSize,
                                          String coinSymbol,
                                          String baseSymbol,
                                          ExchangeOrderDirection direction,
                                          ExchangeOrderStatus status,
                                          Long startTime,
                                          Long endTime);

    /**
     * 获取撮合明细
     *
     * @param orderId 订单号
     * @return 撮合明细列表
     */
    List<ExchangeOrderDetail> listTradeDetail(String orderId);


    /**
     * 历史订单明细查询
     *
     * @param orderId
     * @return
     */
    List<ExchangeOrderDetail> listHistoryByOrderId(String orderId);
}
