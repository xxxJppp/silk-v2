package com.spark.bitrade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  手续费统计
 *
 * @author young
 * @time 2019.12.04 15:21
 */
@Data
@ApiModel(description = "手续费统计")
public class FeeStats {
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String coinUnit;


    /**
     * 交易手续费
     */
    @ApiModelProperty(value = "交易手续费", example = "")
    private BigDecimal fee;
}
