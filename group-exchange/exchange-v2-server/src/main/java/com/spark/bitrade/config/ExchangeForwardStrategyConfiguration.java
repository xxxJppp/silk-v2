package com.spark.bitrade.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *  转发策略
 *
 * @author young
 * @time 2019.12.25 14:24
 */
@Data
public class ExchangeForwardStrategyConfiguration {
    // 转发：下单、撤单、成交明细、成交订单、重做

    /**
     * 必填，策略ID，唯一标识
     */
    @NotBlank
    private String strategyId;

    /**
     * 必填，转发服务名称
     */
    private String applicationName;

    /**
     * 必填，转发服务的服务路径
     */
    private String serverContextPath;

    /**
     * 下单买入转发
     */
    private Boolean enablePlaceBuy = false;

    /**
     * 下单卖出转发
     */
    private Boolean enablePlaceSell = false;


    /**
     * 撤单买单转发
     */
    private Boolean enableCancelOrderBuy = false;

    /**
     * 撤单卖单转发
     */
    private Boolean enableCancelOrderSell = false;

    /**
     * 成交明细买单转发
     */
    private Boolean enableTradeBuy = false;

    /**
     * 成交明细卖单转发
     */
    private Boolean enableTradeSell = false;

    /**
     * 成交买单转发
     */
    private Boolean enableCompleteOrderBuy = false;

    /**
     * 成交卖单转发
     */
    private Boolean enableCompleteOrderSell = false;
}
