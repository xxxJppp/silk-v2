package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 本金返还计划表(LockSlpReleasePlan)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "本金返还计划表")
public class LockSlpReleasePlan {

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
     * 套餐名称
     */
    @ApiModelProperty(value = "套餐名称", example = "")
    private String planName;

    /**
     * 关联的活动锁仓记录ID
     */
    @ApiModelProperty(value = "关联的活动锁仓记录ID", example = "")
    private Long refLockDetailId;

    /**
     * 投入币数
     */
    @ApiModelProperty(value = "投入币数", example = "")
    private BigDecimal lockAmount;

    /**
     * 锁仓奖励倍数
     */
    @ApiModelProperty(value = "锁仓奖励倍数", example = "")
    private BigDecimal zoomScale;

    /**
     * 业绩币数
     */
    @ApiModelProperty(value = "业绩币数", example = "")
    private BigDecimal realAmount;

    /**
     * 锁仓奖励基金币数
     */
    @ApiModelProperty(value = "锁仓奖励基金币数", example = "")
    private BigDecimal planIncome;

    /**
     * 锁仓奖励基金剩余币数
     */
    @ApiModelProperty(value = "锁仓奖励基金剩余币数", example = "")
    private BigDecimal remainAmount;

    /**
     * 返还总期数
     */
    @ApiModelProperty(value = "返还总期数", example = "")
    private Integer releaseTotalTimes;

    /**
     * 每日释放比例
     */
    @ApiModelProperty(value = "每日释放比例", example = "")
    private BigDecimal releaseRate;

    /**
     * 当前返还期数：每期返还后更新更新
     */
    @ApiModelProperty(value = "当前返还期数：每期返还后更新更新", example = "")
    private Integer releaseCurrentTimes;

    /**
     * 当前返还时间：每期返还后更新更新
     */
    @ApiModelProperty(value = "当前返还时间：每期返还后更新更新", example = "")
    private Date releaseCurrentDate;

    /**
     * 状态（0=进行中、1=已完成）
     */
    @ApiModelProperty(value = "状态（0=进行中、1=已完成）", example = "")
    private SlpStatus status;

    /**
     * 更新任务状态（0=未处理，1=已处理）
     */
    @ApiModelProperty(value = "更新任务状态（0=未处理，1=已处理）", example = "")
    private SlpProcessStatus taskStatus;

    /**
     * 关联的前一条返回计划ID
     */
    @ApiModelProperty(value = "关联的前一条返回计划ID", example = "")
    private Long refPrevId;

    /**
     * 关联的前一条返回计划名称
     */
    @ApiModelProperty(value = "关联的前一条返回计划名称", example = "")
    private String refPrevPlanName;

//    /**
//     * 关联的后一条返回计划ID
//     */
//    @ApiModelProperty(value = "关联的后一条返回计划ID", example = "")
//    private Long refNextId;
//
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String comment;


}