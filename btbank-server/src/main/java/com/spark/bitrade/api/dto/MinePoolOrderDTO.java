package com.spark.bitrade.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author davi
 */
@Data
@ApiModel(value = "矿池订单")
public class MinePoolOrderDTO {
    @ApiModelProperty(value = "订单ID")
    private Long id;
    @ApiModelProperty(value = "总额")
    private BigDecimal price;
    @ApiModelProperty(value = "订单状态：0 新订单，1 已抢单，2 已派单,3抢单结算完成，4，派单结算完成)")
    private Integer status;
}
