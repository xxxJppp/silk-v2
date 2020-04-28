package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.PromotionRewardCycle;
import com.spark.bitrade.constant.RewardRecordLevel;
import com.spark.bitrade.constant.RewardRecordStatus;
import com.spark.bitrade.constant.RewardRecordType;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (RewardRecord)表实体类
 *
 * @author yangch
 * @since 2019-11-01 15:13:07
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class RewardRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 数目
     */
    @ApiModelProperty(value = "数目", example = "")
    private BigDecimal amount;

    @ApiModelProperty(value = "创建时间", example = "")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "备注", example = "")
    private String remark;

    @ApiModelProperty(value = "返佣类型", example = "")
    private RewardRecordType type;

    @ApiModelProperty(value = "币种ID", example = "")
    private String coinId;

    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 返佣级别（1级2级3级）
     */
    @ApiModelProperty(value = "返佣级别（1级2级3级）", example = "")
    private RewardRecordLevel level;

    /**
     * 关联交易记录ID，引用member_transaction表
     */
    @ApiModelProperty(value = "关联交易记录ID", example = "")
    private Long refTransactionId;

    /**
     * 返佣状态（未发放、发放中、已发放）
     */
    @ApiModelProperty(value = "返佣状态（未发放、发放中、已发放）", example = "")
    private RewardRecordStatus status;

    /**
     * 返佣时间
     */
    @ApiModelProperty(value = "返佣时间", example = "")
    private Date treatedTime;

    /**
     * 兑换汇率
     */
    @ApiModelProperty(value = "兑换汇率", example = "")
    private BigDecimal exchangeRate;

    /**
     * 来源数目
     */
    @ApiModelProperty(value = "来源数目", example = "")
    private BigDecimal fromAmount;

    /**
     * 奖励的来源币种，如SLB
     */
    @ApiModelProperty(value = "奖励的来源币种，如SLB", example = "")
    private String fromCoinUnit;

    /**
     * 奖励的来源会员ID
     */
    @ApiModelProperty(value = "奖励的来源会员ID", example = "")
    private Long fromMemberId;

    /**
     * 返佣周期的冗余字段（实时、天、周、月）
     */
    @ApiModelProperty(value = "返佣周期的冗余字段（实时、天、周、月）", example = "")
    private PromotionRewardCycle rewardCycle;


}