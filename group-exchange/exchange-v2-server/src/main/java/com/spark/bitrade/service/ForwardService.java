package com.spark.bitrade.service;

import com.spark.bitrade.config.ExchangeForwardStrategyConfiguration;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *  
 *
 * @author young
 * @time 2020.01.02 11:25
 */
public interface ForwardService {
    /**
     * 获取策略
     *
     * @param symbol 交易对
     * @return
     */
    Optional<ExchangeForwardStrategyConfiguration> getStrategy(String symbol);

    /**
     * 委托订单
     *
     * @param memberId     会员ID
     * @param direction    交易方式：买币、卖币
     * @param symbol       交易对
     * @param price        委托价格
     * @param amount       委托数量
     * @param type         订单类型：市价、限价
     * @param tradeCaptcha 交易验证码
     * @return
     */
    MessageRespResult<ExchangeOrder> placeOrder(Long memberId,
                                                ExchangeOrderDirection direction,
                                                String symbol,
                                                BigDecimal price,
                                                BigDecimal amount,
                                                ExchangeOrderType type,
                                                String tradeCaptcha);

    /**
     * 处理交易明细的买单
     *
     * @param trade 交易明细
     * @return
     */
    MessageRespResult<ExchangeOrder> tradeBuy(@RequestBody ExchangeTrade trade);


    /**
     * 处理交易明细的卖单
     *
     * @param trade 交易明细
     * @return
     */
    MessageRespResult<ExchangeOrder> tradeSell(@RequestBody ExchangeTrade trade);

    /**
     * 完成订单的处理
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 成交数量
     * @param turnover     成交额
     * @return
     */
    MessageRespResult<ExchangeOrder> completedOrder(String symbol, @RequestParam("memberId") Long memberId,
                                                    @RequestParam("orderId") String orderId,
                                                    @RequestParam("tradedAmount") BigDecimal tradedAmount,
                                                    @RequestParam("turnover") BigDecimal turnover);

    /**
     * 撤销订单的处理
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 成交数量
     * @param turnover     成交额
     * @return
     */
    MessageRespResult<ExchangeOrder> canceledOrder(String symbol, @RequestParam("memberId") Long memberId,
                                                   @RequestParam("orderId") String orderId,
                                                   @RequestParam("tradedAmount") BigDecimal tradedAmount,
                                                   @RequestParam("turnover") BigDecimal turnover);

    /**
     * 撤销订单的处理（无法提供成交额和成交量）
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    MessageRespResult<ExchangeOrder> canceledOrder(String symbol, @RequestParam("memberId") Long memberId,
                                                   @RequestParam("orderId") String orderId);

    /**
     * 重做
     *
     * @param id
     * @return
     */
    MessageRespResult redo(@RequestParam("id") Long id, String symbol);
}
