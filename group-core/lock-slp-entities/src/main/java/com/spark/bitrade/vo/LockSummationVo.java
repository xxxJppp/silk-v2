package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SLP加速释放页面，锁仓汇总VO
 *
 * @author zhongxj
 * @since 2019-07-10 09:27:03
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "锁仓汇总VO")
public class LockSummationVo {

    /**
     * 总锁仓数量
     */
    @ApiModelProperty(value = "总锁仓数量", example = "")
    private BigDecimal totalLock;

    /**
     * 锁仓汇总明细
     */
    @ApiModelProperty(value = "锁仓汇总明细", example = "")
    List<LockSlpMemberSummaryVo> lockSlpMemberSummaryVoList;
}
