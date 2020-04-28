package com.spark.bitrade.service;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 *  创建订单、查询订单服务接口
 *
 * @author young
 * @time 2019.09.06 18:01
 */
@FeignClient("service-exchange-v2-release")
public interface IExchange2ReleaseService {
    String uri_prefix = "/exchange2-release/service/v1/order/";


    /**
     * 创建订单
     *
     * @param memberId     用户ID
     * @param direction    订单方向
     * @param symbol       交易对
     * @param price        委托价
     * @param amount       委托数量
     * @param type         交易类型
     * @param tradeCaptcha 交易验证码
     * @return
     */
    @PostMapping(value = uri_prefix + "place")
    MessageRespResult placeOrder(@RequestParam("memberId") Long memberId,
                                 @RequestParam("direction") ExchangeOrderDirection direction,
                                 @RequestParam("symbol") String symbol,
                                 @RequestParam("price") BigDecimal price,
                                 @RequestParam("amount") BigDecimal amount,
                                 @RequestParam("type") ExchangeOrderType type,
                                 @RequestParam(value = "tradeCaptcha", required = false) String tradeCaptcha);

    /**
     * 处理交易明细的买单
     *
     * @param trade 交易明细
     * @return
     */
    @PostMapping(value = uri_prefix + "tradeBuy")
    MessageRespResult<ExchangeOrder> tradeBuy(@RequestBody ExchangeTrade trade);
}
