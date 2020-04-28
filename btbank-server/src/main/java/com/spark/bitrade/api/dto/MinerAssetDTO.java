package com.spark.bitrade.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author davi
 */
@Data
@ApiModel(value = "矿工资产信息")
public class MinerAssetDTO {
    @ApiModelProperty(value = "矿池总额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "矿池可用")
    private BigDecimal usedAmount;

    @ApiModelProperty(value = "矿池锁仓")
    private BigDecimal lockedAmount;

    @ApiModelProperty(value = "矿池佣金")
    private BigDecimal rewardAmount;

    @ApiModelProperty(value = "历史佣金")
    private BigDecimal gotRewardAmount;
}
