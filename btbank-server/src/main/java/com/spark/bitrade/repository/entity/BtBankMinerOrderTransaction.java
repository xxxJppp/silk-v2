package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankMinerOrderTransaction")
@Data
@TableName(value = "bt_bank_miner_order_transaction")
public class BtBankMinerOrderTransaction implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 矿工订单ID
     */
    @TableField(value = "miner_order_id")
    @ApiModelProperty(value = "矿工订单ID")
    private Long minerOrderId;

    /**
     * 矿工ID
     */
    @TableField(value = "member_id")
    @ApiModelProperty(value = "矿工ID")
    private Long memberId;

    /**
     * 佣金金额
     */
    @TableField(value = "reward_amount")
    @ApiModelProperty(value = "佣金金额")
    private BigDecimal rewardAmount;

    /**
     * 金额
     */
    @TableField(value = "money")
    @ApiModelProperty(value = "金额")
    private BigDecimal money;

    /**
     * 0,新订单, 1,抢单，2派单，3，抢单结算，4，派单结算， 5,订单派单超时
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "1,抢单，2派单，3，抢单结算，4，派单结算, 5,订单派单超时")
    private Integer type;

    /**
     * 解锁时间
     */
    @TableField(value = "unlock_time")
    @ApiModelProperty(value = "解锁时间")
    private Date unlockTime;

    private static final long serialVersionUID = 1L;
}