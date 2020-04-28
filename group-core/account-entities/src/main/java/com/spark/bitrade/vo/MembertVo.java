package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目方持仓统计用户明细实体
 *
 * @author: Zhong Jiang
 * @time: 2019.11.11 11:27
 */
@Data
@ApiModel(description = "用户持仓统计明细Vo")
public class MembertVo {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long membertId;

    /**
     * 用户名字
     */
    @ApiModelProperty(value = "用户名字", example = "")
    private String memberName;

    /**
     * 用户持币数
     */
    @ApiModelProperty(value = "用户持币数", example = "")
    private BigDecimal memberHoldCoinNum;

    /**
     * 持仓币数
     */
    @ApiModelProperty(value = "持仓币数", example = "")
    private BigDecimal holdCoinNum;

    /**
     * 持仓用户数
     */
    @ApiModelProperty(value = "持仓用户数", example = "")
    private Integer holdUserNum;

    /**
     * 持仓币数
     */
    @ApiModelProperty(value = "持仓币数", example = "")
    private BigDecimal holdCoinNums;

    /**
     * 有效用户数
     */
    @ApiModelProperty(value = "有效用户数", example = "")
    private Integer effectiveUserNum;

    /**
     * 有效用户持仓币数
     */
    @ApiModelProperty(value = "有效用户持仓币数", example = "")
    private BigDecimal holdEffectiveUserNum;
}
