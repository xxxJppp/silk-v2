package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员钱包表实体类
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
@Data
@ApiModel(description = "会员余额")
public class MemberWalletVo {


    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额", example = "")
    private BigDecimal balance;

    /**
     * 币种ID
     */
    @ApiModelProperty(value = "币种ID", example = "")
    private String unit;

    /**
     * 币种精度
     */
    @ApiModelProperty(value = "币种精度", example = "")
    private Integer scale;

    /**
     * 最高单笔交易
     */
    @ApiModelProperty(value = "最高单笔交易", example = "")
    private BigDecimal tradeMax;

    /**
     * 最低单笔交易
     */
    @ApiModelProperty(value = "最低单笔交易", example = "")
    private BigDecimal tradeMin;

    /**
     * 费率下调系数
     */
    @ApiModelProperty(value = "费率下调系数", example = "")
    private BigDecimal rateReductionFactor;


    public MemberWalletVo() {
    }

    public MemberWalletVo(BigDecimal balance, String unit, Integer scale) {
        this.balance = balance;
        this.unit = unit;
        this.scale = scale;
    }


}
