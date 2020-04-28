package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.TransactionType;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (MemberTransaction)表实体类
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class MemberTransaction {

    /**
     * 交易记录编号
     */
    @TableId
    @ApiModelProperty(value = "交易记录编号", example = "")
    private Long id;

    /**
     * 充值或提现地址、或转账地址
     */
    @ApiModelProperty(value = "充值或提现地址、或转账地址", example = "")
    private String address;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额", example = "")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 币种名称，如 BTC
     */
    @ApiModelProperty(value = "币种名称，如 BTC", example = "")
    private String symbol;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型", example = "")
    private TransactionType type;

    /**
     * 交易手续费
     */
    @ApiModelProperty(value = "交易手续费", example = "")
    private BigDecimal fee;

    /**
     * 标识位，特殊情况会用到，默认为0
     */
    @ApiModelProperty(value = "标识位，特殊情况会用到，默认为0", example = "")
    private Integer flag;

    /**
     * 关联单号
     */
    @ApiModelProperty(value = "关联单号", example = "")
    private String refId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String comment;

    /**
     * 优惠手续费
     */
    @ApiModelProperty(value = "优惠手续费", example = "")
    private BigDecimal feeDiscount;

    /**
     * 实现使用其他币种抵扣手续费:手续费抵扣币种单位（不包括当前币种）
     */
    @ApiModelProperty(value = "手续费抵扣币种单位", example = "")
    private String feeDiscountCoinUnit;

    /**
     * 实现使用其他币种抵扣手续费:抵扣币种对应手续费
     */
    @ApiModelProperty(value = "抵扣币种对应手续费", example = "")
    private BigDecimal feeDiscountAmount;

}