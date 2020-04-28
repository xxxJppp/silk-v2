/**
 * silktrader-platform-v2
 * <p>
 * Copyright 2014 Acooly.cn, Inc. All rights reserved.
 *
 * @author SilkTouch
 * @date 2020-04-08 16:55
 */
package com.spark.bitrade.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 *
 * @author 舒世平
 * @date 2020-04-08 16:55
 */
@Slf4j
@Data
@ApiModel
public class ExchangeUsdcInfo {
    /**
     * 优惠剩余额度
     */
    @ApiModelProperty("优惠剩余额度")
    private BigDecimal remainingAmountOfDiscount;
    /**
     * USDC兑换币种
     */
    @ApiModelProperty("USDC兑换币种")
    private String coinUnit;
    /**
     * USDC兑换币种余额
     */
    @ApiModelProperty("USDC兑换币种余额")
    private BigDecimal balance;
    /**
     * 兑换总账号的USDC余额
     */
    @ApiModelProperty("兑换总账号的USDC余额")
    private BigDecimal usdcBalance;
    /**
     * USDC单次最大兑换数量
     */
    @ApiModelProperty("USDC单次最大兑换数量")
    private BigDecimal maxLimit;
    /**
     * USDC单次最小兑换数量
     */
    @ApiModelProperty("USDC单次最小兑换数量")
    private BigDecimal minLimit;
    /**
     * USDC对兑换币的价格
     * 如果price=2,则 一个兑换币等于两个USDC
     */
    @ApiModelProperty("USDC对兑换币的价格：如果price=2,则 一个兑换币等于两个USDC")
    private BigDecimal price;
    /**
     * 经纪人兑换USDC优惠比例
     */
    @ApiModelProperty("经纪人兑换USDC优惠比例")
    private String rate;
}
