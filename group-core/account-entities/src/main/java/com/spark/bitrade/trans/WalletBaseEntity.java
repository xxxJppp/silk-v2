package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  账户的基本实体
 * 备注：根据该基本实体可处理 账户变更、交易流水、手续费
 *
 * @author yangch
 * @time 2019.02.13 11:31
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "账户的基本实体")
public class WalletBaseEntity {
    /**
     * 选填，币种ID
     */
    @ApiModelProperty(value = "币种ID", example = "")
    private String coinId;

    /**
     * 必填，币种单位
     */
    @ApiModelProperty(value = "币种单位", example = "")
    private String coinUnit;

    /**
     * 变动的可用余额，整数为添加/负数为减少
     */
    @ApiModelProperty(value = "变动的可用余额", example = "")
    private BigDecimal tradeBalance = BigDecimal.ZERO;

    /**
     * 变动的冻结余额，整数为添加/负数为减少
     */
    @ApiModelProperty(value = "变动的冻结余额", example = "")
    private BigDecimal tradeFrozenBalance = BigDecimal.ZERO;

    /**
     * 变动的锁仓余额，整数为添加/负数为减少
     */
    @ApiModelProperty(value = "变动的锁仓余额", example = "")
    private BigDecimal tradeLockBalance = BigDecimal.ZERO;

    /**
     * 选填，手续费（如有手续费，则提供相应的手续费）
     */
    @ApiModelProperty(value = "手续费", example = "")
    private ServiceChargeEntity serviceCharge;

    /**
     * 选填，交易备注信息
     */
    @ApiModelProperty(value = "交易备注", example = "")
    private String comment;
}
