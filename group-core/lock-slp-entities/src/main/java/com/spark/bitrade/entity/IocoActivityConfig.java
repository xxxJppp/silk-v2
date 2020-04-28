package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ioco活动配置(IocoActivityConfig)表实体类
 *
 * @author daring5920
 * @since 2019-07-03 14:45:18
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "ioco活动配置")
public class IocoActivityConfig {

    /**
     * 记录id
     */
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "记录id", example = "")
    private Long id;

    /**
     * 活动期数
     */
    @ApiModelProperty(value = "活动期数", example = "")
    private Integer acivityPeriod;

    /**
     * 前台活动url
     */
    @ApiModelProperty(value = "前台活动url", example = "")
    private String activityUrl;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String activityName;

    /**
     * 活动状态0未生效，1生效，2失效
     */
    @ApiModelProperty(value = "活动状态0未生效，1生效，2失效", example = "")
    private Integer status;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间", example = "")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @ApiModelProperty(value = "活动结束时间", example = "")
    private Date endTime;

    /**
     * 每份对应usdt数量
     */
    @ApiModelProperty(value = "每份对应usdt数量", example = "")
    private BigDecimal usdtAmount;

    /**
     * 每份对应bt数量
     */
    @ApiModelProperty(value = "每份对应bt数量", example = "")
    private BigDecimal btAmount;

    /**
     * 每份对应slp数量
     */
    @ApiModelProperty(value = "每份对应slp数量", example = "")
    private BigDecimal slpAmount;

    /**
     * 该活动总的slp申购数量
     */
    @ApiModelProperty(value = "该活动总的slp申购数量", example = "")
    private BigDecimal activitTotalSlpAmount;

    /**
     * 该活动总的slp申购剩余数量
     */
    @ApiModelProperty(value = "该活动总的slp申购剩余数量", example = "")
    private BigDecimal activitTotalSlpBalance;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @TableField(value="create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @TableField(value="update_time", fill = FieldFill.INSERT_UPDATE, update="NOW()")
    private Date updateTime;


}