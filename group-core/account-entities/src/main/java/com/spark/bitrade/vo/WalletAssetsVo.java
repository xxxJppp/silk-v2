package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * WalletAssetsVo
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 17:38
 */
@Data
@ApiModel(description = "钱包资产VO")
public class WalletAssetsVo {


    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String coinUnit;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额", example = "")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "冻结余额", example = "")
    private BigDecimal frozen;

    /**
     * 锁定余额
     */
    @ApiModelProperty(value = "锁定余额", example = "")
    private BigDecimal locked;

    /**
     * 获取总额
     *
     * @return total
     */
    public BigDecimal getTotal() {
        BigDecimal sum = BigDecimal.ZERO;

        if (balance != null) {
            sum = sum.add(balance);
        }
        if (frozen != null) {
            sum = sum.add(frozen);
        }
        if (locked != null) {
            sum = sum.add(locked);
        }

        return sum;
    }
}
