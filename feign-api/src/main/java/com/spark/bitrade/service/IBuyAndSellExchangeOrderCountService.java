package com.spark.bitrade.service;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.15 16:19
 */
@FeignClient(FeignServiceConstant.EXCHANGE_SERVER2)
public interface IBuyAndSellExchangeOrderCountService {

    /**
     * 成交买盘明细
     * @return
     */
    @GetMapping("/exchange2/service/v1/order/stats")
    MessageRespResult orderStats(@RequestParam("coinSymbol") String coinSymbol ,
                                 @RequestParam("type")ExchangeOrderType type ,
                                 @RequestParam("startTime")Long start,
                                 @RequestParam("endTime")Long end);
    /**
     * 买卖盘列表查询
     * @return
     */
    @PostMapping("/exchange2/service/v1/order/historyOrders")
    MessageRespResult exchangeHistoryOrders(@RequestParam("current") Integer current,
                                            @RequestParam("size") Integer size,
                                            @RequestParam("memberId") Long memberId,
                                            @RequestParam(value = "coinSymbol") String coinSymbol,
                                            @RequestParam(value = "direction") ExchangeOrderDirection direction,
                                            @RequestParam(value = "status") ExchangeOrderStatus status,
                                            @RequestParam("startTime")Long start,
                                            @RequestParam("endTime")Long end);
}
