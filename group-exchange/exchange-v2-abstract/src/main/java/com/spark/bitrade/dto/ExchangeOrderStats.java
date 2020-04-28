package com.spark.bitrade.dto;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  项目方中心 统计功能
 *
 * @author young
 * @time 2019.11.27 15:27
 */
@Data
@ApiModel(description = "项目方中心统计")
public class ExchangeOrderStats {
    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private long memberId;

    /**
     * 基币
     */
    @ApiModelProperty(value = "基币", example = "")
    private String baseSymbol;

    /**
     * 订单方向 0买，1卖
     */
    @ApiModelProperty(value = "订单方向 0买，1卖", example = "")
    private ExchangeOrderDirection direction;


    /**
     * 交易笔数
     */
    @ApiModelProperty(value = "交易笔数", example = "")
    private Integer tradeCount;

    /**
     * 成交总额
     */
    @ApiModelProperty(value = "成交总额", example = "")
    private BigDecimal tradeTurnover;
}
