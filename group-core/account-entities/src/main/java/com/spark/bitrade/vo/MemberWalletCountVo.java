package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目持仓统计实体
 *
 * @author: Zhong Jiang
 * @time: 2019.11.11 11:23
 */
@Data
@ApiModel(description = "项目方持仓统计Vo")
public class MemberWalletCountVo {

    /**
     * 持仓用户数
     */
    @ApiModelProperty(value = "持仓用户数", example = "")
    private Integer holdUserNum;

    /**
     * 持仓币数
     */
    @ApiModelProperty(value = "持仓币数", example = "")
    private BigDecimal holdCoinNum;

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

    /**
     * 持仓用户明细
     */
    @ApiModelProperty(value = "持仓用户明细", example = "")
    private List<MembertVo> membertVoList;

    private Integer total;

    private Integer current;

    private Integer size;

}
