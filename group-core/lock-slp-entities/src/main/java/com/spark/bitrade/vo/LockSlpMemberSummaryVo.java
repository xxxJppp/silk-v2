package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * SLP加速释放页面，锁仓汇总明细VO
 *
 * @author zhongxj
 * @since 2019-07-10 09:27:03
 */
@Data
public class LockSlpMemberSummaryVo {
    /**
     * 直推部门ID
     */
    @ApiModelProperty(value = "直推部门ID", example = "")
    private Long memberId;

    /**
     * 锁仓数量
     */
    @ApiModelProperty(value = "锁仓数量", example = "")
    private BigDecimal totalValidAmount;

    /**
     * 团队锁仓数量
     */
    @ApiModelProperty(value = "团队锁仓数量", example = "")
    private BigDecimal totalSubValidAmount;
}
