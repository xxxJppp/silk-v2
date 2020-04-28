package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 15:22
 */
@Data
/**
 * Vip费用Vo
 */
public class CurrentAmountVo {

    /**
     * 支付类型
     * 10-购买
     * 20-锁仓
     */
    @ApiModelProperty(value = "{10-购买， 20-锁仓 }")
    private Integer payType;

    @ApiModelProperty(value = "购买费用")
    private BigDecimal buyAmount;

    @ApiModelProperty(value = "锁仓费用")
    private BigDecimal lockAmount;


}
