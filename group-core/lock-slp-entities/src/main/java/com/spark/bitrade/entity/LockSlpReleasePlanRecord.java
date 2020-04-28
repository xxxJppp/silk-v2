package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpStatus;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 本金返还记录表(LockSlpReleasePlanRecord)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "本金返还记录表")
public class LockSlpReleasePlanRecord {

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id", example = "")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种，必须大写
     */
    @ApiModelProperty(value = "币种，必须大写", example = "")
    private String coinUnit;

    /**
     * 分期数：从1开始
     */
    @ApiModelProperty(value = "分期数：从1开始", example = "")
    private Integer period;

    /**
     * 返还时间
     */
    @ApiModelProperty(value = "返还时间", example = "")
    private Date releaseTime;

    /**
     * 分配比例：冗余，可用余额和奖池的分配比例
     */
    @ApiModelProperty(value = "分配比例：冗余，可用余额和奖池的分配比例", example = "")
    private BigDecimal allocationProportion;

    /**
     * 返还币数：返回到可用余额
     */
    @ApiModelProperty(value = "返还币数：返回到可用余额", example = "")
    private BigDecimal releaseAmount;

    /**
     * 奖池贡献币数：返还到奖池数量
     */
    @ApiModelProperty(value = "奖池贡献币数：返还到奖池数量", example = "")
    private BigDecimal jackpotAmount;

    /**
     * 每日释放比例
     */
    @ApiModelProperty(value = "每日释放比例", example = "")
    private BigDecimal releaseRate;

    /**
     * 状态（0=进行中、1=已完成）
     */
    @ApiModelProperty(value = "状态（0=进行中、1=已完成）", example = "")
    private SlpStatus status;

    /**
     * 关联的“本金返回计划表.记录ID”
     */
    @ApiModelProperty(value = "关联的“本金返回计划表.记录ID”", example = "")
    private Long refPlanId;

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

    /**
     * 加速释放任务处理状态（0-未处理，1-已处理）
     */
    @ApiModelProperty(value = "加速释放任务处理状态（0-未处理，1-已处理）", example = "")
    private SlpProcessStatus releaseTaskStatus;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String comment;

    /**
     * 记录校验码
     */
    @ApiModelProperty(value = "记录校验码", example = "")
    private String signature;


    /**
     * 释放币种，必须大写
     */
    @ApiModelProperty(value = "释放币种，必须大写", example = "")
    private String coinInUnit;
    /**
     * 释放的兑换汇率
     */
    @ApiModelProperty(value = "'释放的兑换汇率'", example = "")
    private BigDecimal releaseInRate;
    /**
     * 释放的币数：返回到可用余额
     */
    @ApiModelProperty(value = "'释放的币数：返回到可用余额'", example = "")
    private BigDecimal releaseInAmount;
    /**
     * 释放的奖池币数：返回到可用余额
     */
    @ApiModelProperty(value = "释放的奖池币数：返回到可用余额", example = "")
    private BigDecimal jackpotInAmount;
}