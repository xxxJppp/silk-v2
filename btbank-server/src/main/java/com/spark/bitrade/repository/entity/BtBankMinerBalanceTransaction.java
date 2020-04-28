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

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankMinerBalanceTransaction")
@Data
@TableName(value = "bt_bank_miner_balance_transaction")
public class BtBankMinerBalanceTransaction implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 矿工ID
     */
    @TableField(value = "member_id")
    @ApiModelProperty(value = "矿工ID")
    private Long memberId;

    /**
     * 类型：1  转入，2 抢单本金转出，3 抢单佣金转入，4 抢单佣金转出，5 派单本金转出，6 派单佣金转入，7 派单佣金转出，8 转出，9 固定佣金转出，10 固定佣金转入，11 抢单锁仓，12 派单锁仓
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "类型：1  转入，2 抢单本金转出，3 抢单佣金转入，4 抢单佣金转出，5 派单本金转出，6 派单佣金转入，7 派单佣金转出，8 转出，9 固定佣金转出，10 固定佣金转入，11 抢单锁仓，12 派单锁仓")
    private Integer type;

    /**
     * 订单金额
     */
    @TableField(value = "money")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal money;

    /**
     * 当前余额
     */
    @TableField(value = "balance")
    @ApiModelProperty(value = "当前余额")
    private BigDecimal balance;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 订单流水ID 转入转出流水无此ID
     */
    @TableField(value = "order_transaction_id")
    @ApiModelProperty(value = "订单流水ID    转入转出流水无此ID")
    private Long orderTransactionId;

    /**
     * 关联id
     */
    @TableField(value = "ref_id")
    @ApiModelProperty(value = "关联id")
    private Long refId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    private static final long serialVersionUID = 1L;
}