package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  布朗计划用户加速释放当前节点以及分类加速汇总
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.10 09:31  
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "布朗计划用户加速释放当前节点以及分类加速汇总")
public class LockSlpCurrentNodeVo {

    /**
     * 社区节点名称
     */
    @ApiModelProperty(value = "当前社区节点名称", example = "水星")
    private String currentLevelName;

    /**
     * 直推加速释放
     */
    @ApiModelProperty(value = "直推加速释放", example = "100.00")
    private BigDecimal directReleasedReward = BigDecimal.ZERO;

    /**
     * 社区加速释放
     */
    @ApiModelProperty(value = "社区加速释放", example = "1000.00")
    private BigDecimal communityReleasedReward = BigDecimal.ZERO;

    /**
     * 太阳特权奖励
     */
    @ApiModelProperty(value = "太阳特权奖励", example = "200.00")
    private BigDecimal sunReleasedReward = BigDecimal.ZERO;
}


















