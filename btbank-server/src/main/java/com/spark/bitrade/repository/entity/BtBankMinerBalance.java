package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankMinerBalance")
@Data
@TableName(value = "bt_bank_miner_balance")
public class BtBankMinerBalance implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "member_id")
    @ApiModelProperty(value = "用户ID")
    private Long memberId;

    /**
     * 可用余额
     */
    @TableField(value = "balance_amount")
    @ApiModelProperty(value = "可用余额")
    private BigDecimal balanceAmount;

    /**
     * 锁仓余额
     */
    @TableField(value = "lock_amount")
    @ApiModelProperty(value = "锁仓余额")
    private BigDecimal lockAmount;

    /**
     * 进中的佣金统计
     */
    @TableField(value = "processing_reward_sum")
    @ApiModelProperty(value = "进中的佣金统计")
    private BigDecimal processingRewardSum;

    /**
     * 已得到的佣金统计
     */
    @TableField(value = "got_reward_sum")
    @ApiModelProperty(value = "已得到的佣金统计")
    private BigDecimal gotRewardSum;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}