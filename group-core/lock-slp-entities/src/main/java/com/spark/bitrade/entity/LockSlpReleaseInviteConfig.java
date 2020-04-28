package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分享收益配置表(LockSlpReleaseInviteConfig)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "分享收益配置表")
public class LockSlpReleaseInviteConfig {

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id", example = "")
    private Long id;

    /**
     * 币种，必须大写
     */
    @ApiModelProperty(value = "币种，必须大写", example = "")
    private String coinUnit;

    /**
     * 直推用户数量
     */
    @ApiModelProperty(value = "直推用户数量", example = "")
    private Integer promotionCount;

    /**
     * 享1代理财收益的百分比
     */
    @ApiModelProperty(value = "享1代理财收益的百分比", example = "")
    private BigDecimal generationOneRate;

    /**
     * 享2代理财收益的百分比
     */
    @ApiModelProperty(value = "享2代理财收益的百分比", example = "")
    private BigDecimal generationTwoRate;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;


}