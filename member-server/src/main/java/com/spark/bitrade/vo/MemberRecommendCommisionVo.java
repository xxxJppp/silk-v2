package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-27 18:56
 */
@Data
public class MemberRecommendCommisionVo {

    /**
     * 币币交易
     */
    @ApiModelProperty(value = "币币交易")
    private BigDecimal countExchange = BigDecimal.ZERO;

    /**
     * 会员返佣
     */
    @ApiModelProperty(value = "会员返佣")
    private BigDecimal countCommision = BigDecimal.ZERO;

    /**
     * 以获取佣金
     */
    @ApiModelProperty(value = "以获取佣金")
    private BigDecimal sumCount = BigDecimal.ZERO;

    private String commisionUnit = "";

}
