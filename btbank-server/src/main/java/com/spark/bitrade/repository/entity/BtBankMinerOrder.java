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

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankMinerOrder")
@Data
@TableName(value = "bt_bank_miner_order")
public class BtBankMinerOrder implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "null")
    private Long id;

    /**
     * 上游订单ID
     */
    @TableField(value = "upstream_order_id")
    @ApiModelProperty(value = "上游订单ID")
    private String upstreamOrderId;

    /**
     * 订单金额
     */
    @TableField(value = "money")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal money;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 订单状态:(0 新订单，1 已抢单，2 已派单,3抢单结算完成，4，派单结算完成)
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "订单状态:(0 新订单，1 已抢单，2 已派单,3抢单结算完成，4，派单结算完成)")
    private Integer status;

    /**
     * 操作时间
     */
    @TableField(value = "process_time")
    @ApiModelProperty(value = "操作时间")
    private Date processTime;

    /**
     * 矿工ID
     */
    @TableField(value = "member_id")
    @ApiModelProperty(value = "矿工ID")
    private Long memberId;

    private static final long serialVersionUID = 1L;
}