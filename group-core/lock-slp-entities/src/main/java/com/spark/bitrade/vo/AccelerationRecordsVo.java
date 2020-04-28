package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.SlpReleaseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SLP加速释放页面，加速记录VO
 *
 * @author zhongxj
 * @since 2019-07-09 14:27:03
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "加速记录VO")
public class AccelerationRecordsVo {
    /**
     * 直推ID
     */
    @ApiModelProperty(value = "直推ID", example = "")
    private Long memberId;

    /**
     * 释放类型（0-直推加速、1-社区加速、2-社区加速、3-太阳特权）
     */
    @ApiModelProperty(value = "释放类型（0-直推加速、1-社区加速、2-社区加速、3-太阳特权）", example = "")
    private SlpReleaseType type;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal releaseInAmount;

    /**
     * 收益时间
     */
    @ApiModelProperty(value = "收益时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date releaseTime;

}
