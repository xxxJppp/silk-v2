package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * SLP我的参与页面，静态释放记录VO
 *
 * @author zhangYanjun
 * @time 2019.08.08 15:01
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "SLP我的参与页面，静态释放记录VO")
public class LockSlpPlanRecordsVo {
    /**
     * 释放时间
     */
    @ApiModelProperty(value = "释放时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date releaseTime;
    /**
     * 释放的币数
     */
    @ApiModelProperty(value = "'释放的币数'", example = "")
    private BigDecimal releaseInAmount;
    /**
     * 套餐名称
     */
    @ApiModelProperty(value = "套餐名称", example = "")
    private String planName;
}
