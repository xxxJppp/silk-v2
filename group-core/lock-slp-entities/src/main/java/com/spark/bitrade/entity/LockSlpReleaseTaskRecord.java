package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 推荐人奖励基金释放记录表(LockSlpReleaseTaskRecord)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "推荐人奖励基金释放记录表")
public class LockSlpReleaseTaskRecord {

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
     * 被推荐用户的ID
     */
    @ApiModelProperty(value = "被推荐用户的ID", example = "")
    private Long refInviteesId;

    /**
     * 关联的锁仓用户ID
     */
    @ApiModelProperty(value = "关联的锁仓用户ID", example = "")
    private Long refLockMemberId;

    /**
     * 币种，必须大写
     */
    @ApiModelProperty(value = "币种，必须大写", example = "")
    private String coinUnit;

    /**
     * 释放类型（0-直推奖、1-级差奖、2-平级奖）
     */
    @ApiModelProperty(value = "释放类型（0-直推奖、1-级差奖、2-平级奖、3-太阳奖）", example = "")
    private SlpReleaseType type;

    /**
     * 分配比例：冗余，可用余额和奖池的分配比例
     */
    @ApiModelProperty(value = "分配比例：冗余，可用余额和奖池的分配比例", example = "")
    private BigDecimal allocationProportion;

    /**
     * 释放币数：释放到可用余额
     */
    @ApiModelProperty(value = "释放币数：释放到可用余额", example = "")
    private BigDecimal releaseAmount;

    /**
     * 奖池贡献币数：释放到奖池数量
     */
    @ApiModelProperty(value = "奖池贡献币数：释放到奖池数量", example = "")
    private BigDecimal jackpotAmount;

    /**
     * 每日释放比例，冗余
     */
    @ApiModelProperty(value = "每日释放比例，冗余", example = "")
    private BigDecimal releaseRate;

    /**
     * 状态（0-待返还、1-已返还）
     */
    @ApiModelProperty(value = "状态（0-待返还、1-已返还）", example = "")
    private SlpStatus status;

    /**
     * 关联的“本金返回计划表.记录ID”
     */
    @ApiModelProperty(value = "关联的“本金返回记录表.记录ID”", example = "")
    private Long refPlanId;

    /**
     * 关联任务表id
     */
    @ApiModelProperty(value = "关联任务表id", example = "")
    private Long taskId;

    /**
     * 返还时间
     */
    @ApiModelProperty(value = "返还时间", example = "")
    private Date releaseTime;

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