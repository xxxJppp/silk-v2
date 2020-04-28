package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpReleaseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 推荐人奖励基金释放任务表(LockSlpReleaseTask)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "推荐人奖励基金释放任务表")
public class LockSlpReleaseTask {

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
     * 释放类型（0-直推奖、1-级差奖、2-平级奖）
     */
    @ApiModelProperty(value = "释放类型（0-直推奖、1-级差奖、2-平级奖、3-太阳奖）", example = "")
    private SlpReleaseType type;

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
     * 关联的活动锁仓记录ID
     */
    @ApiModelProperty(value = "关联的活动锁仓记录ID", example = "")
    private Long refLockDetailId;

    /**
     * 关联的“本金返回计划表.记录ID”
     */
    @ApiModelProperty(value = "关联的“本金返回记录表.记录ID”", example = "")
    private Long refPlanId;

    /**
     * 关联的前一个释放任务的id
     */
    @ApiModelProperty(value = "关联的前一个释放任务的id", example = "")
    private Long refLastTaskId;

    /**
     * 烧伤机制：下级用户锁仓币数
     */
    @ApiModelProperty(value = "烧伤机制：下级用户锁仓币数", example = "")
    private BigDecimal lockAmount;

    /**
     * 烧伤机制：每日释放比例
     */
    @ApiModelProperty(value = "烧伤机制：每日释放比例", example = "")
    private BigDecimal lockRate;

    /**
     * 烧伤机制：我的每日释放比例
     */
    @ApiModelProperty(value = "烧伤机制：我的每日释放比例", example = "")
    private BigDecimal myLockRate;

    /**
     * 直推用户数量，冗余
     */
    @ApiModelProperty(value = "直推用户数量，冗余", example = "")
    private Integer currentPromotionCount;

    /**
     * 子节点理财本金总额
     */
    @ApiModelProperty(value = "子节点理财本金总额", example = "")
    private BigDecimal currentPerformanceAmount;

    /**
     * 会员当前节点等级，冗余
     */
    @ApiModelProperty(value = "会员当前节点等级，冗余", example = "")
    private Long currentLevelId;

    /**
     * 会员当前节点等级名称，冗余
     */
    @ApiModelProperty(value = "会员当前节点等级名称，冗余", example = "")
    private String currentLevelName;

    /**
     * 加速释放奖励率，冗余
     */
    @ApiModelProperty(value = "加速释放奖励率，冗余", example = "")
    private BigDecimal currentReleaseRate;

    /**
     * 推荐关系深度,从1开始
     */
    @ApiModelProperty(value = "推荐关系深度,从1开始", example = "")
    private Integer deep;

    /**
     * 级差奖励率
     */
    @ApiModelProperty(value = "级差奖励率", example = "")
    private BigDecimal rewardRate;

    /**
     * 子部门中的最大奖励率
     */
    @ApiModelProperty(value = "子部门中的最大奖励率", example = "")
    private BigDecimal subMaxRewardRate;

    /**
     * 平级出现次数
     */
    @ApiModelProperty(value = "平级出现次数", example = "")
    private Integer peersTimes;

    /**
     * 记录处理状态（0-未处理、1-已处理）
     */
    @ApiModelProperty(value = "记录处理状态（0-未处理、1-已处理）", example = "")
    private SlpProcessStatus status;

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