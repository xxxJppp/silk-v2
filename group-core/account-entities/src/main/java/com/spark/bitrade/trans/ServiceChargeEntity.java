package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  服务手续费实体
 *  备注：与 账户的基本实体 关联使用
 * @author young
 * @time 2019.06.17 16:17
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "服务手续费实体")
public class ServiceChargeEntity {
    /**
     * 选填，交易手续费
     */
    @ApiModelProperty(value = "交易手续费", example = "")
    private BigDecimal fee = BigDecimal.ZERO;

    /**
     * 选填，优惠手续费（记录交易优惠的手续费）
     */
    @ApiModelProperty(value = "优惠手续费", example = "")
    private BigDecimal feeDiscount = BigDecimal.ZERO;

    /**
     * 选填，实现使用其他币种抵扣手续费:手续费抵扣币种单位（不包括当前币种）
     */
    @ApiModelProperty(value = "手续费抵扣币种单位", example = "")
    private String feeDiscountCoinUnit;

    /**
     * 选填，实现使用其他币种抵扣手续费:抵扣币种对应手续费
     */
    @ApiModelProperty(value = "抵扣币种对应手续费", example = "")
    private BigDecimal feeDiscountAmount = BigDecimal.ZERO;
}
