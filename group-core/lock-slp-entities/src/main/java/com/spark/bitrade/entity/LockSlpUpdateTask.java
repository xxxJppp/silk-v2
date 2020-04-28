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
 * 更新推荐人实时数据任务表(LockSlpUpdateTask)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "更新推荐人实时数据任务表")
public class LockSlpUpdateTask {

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
     * 被推荐用户的ID
     */
    @ApiModelProperty(value = "被推荐用户的ID", example = "")
    private Long refInviteesId;

    /**
     * '推荐人ID，无推荐关系为null'
     */
    @ApiModelProperty(value = "推荐人ID", example = "")
    private Long inviterId;

    /**
     * 关联的锁仓用户ID
     */
    @ApiModelProperty(value = "关联的锁仓用户ID", example = "")
    private Long refLockMemberId;

    /**
     * 关联的活动锁仓记录ID
     */
    @ApiModelProperty(value = "关联的活动锁仓记录ID", example = "")
    private Long refLockDetailId;

    /**
     * 关联的“本金返回计划表.记录ID”
     */
    @ApiModelProperty(value = "关联的“本金返回计划表.记录ID”", example = "")
    private Long refPlanId;

    /**
     * 直推用户数量
     */
    @ApiModelProperty(value = "直推用户数量", example = "")
    private Integer currentPromotionCount;

    /**
     * 子节点数量，默认为0
     */
    @ApiModelProperty(value = "子节点数量，默认为0", example = "")
    private Long currentSubLevelCount;

    /**
     * 子节点理财本金总额
     */
    @ApiModelProperty(value = "子节点理财本金总额", example = "")
    private BigDecimal currentPerformanceAmount;

    /**
     * 社区节点ID
     */
    @ApiModelProperty(value = "社区节点ID", example = "")
    private Long currentLevelId;

    /**
     * 社区节点名称
     */
    @ApiModelProperty(value = "社区节点名称", example = "")
    private String currentLevelName;

    /**
     * 推荐关系深度,从1开始
     */
    @ApiModelProperty(value = "推荐关系深度,从1开始", example = "")
    private Integer deep;

    /**
     * 记录处理状态（0-未处理、1-已处理）
     */
    @ApiModelProperty(value = "记录处理状态（0-未处理、1-已处理）", example = "")
    private SlpStatus status;

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
     * 推荐人社区奖励加速释放处理状态（0-未处理，1-已处理）
     */
    @ApiModelProperty(value = "推荐人社区奖励加速释放处理状态（0-未处理，1-已处理）", example = "")
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


}