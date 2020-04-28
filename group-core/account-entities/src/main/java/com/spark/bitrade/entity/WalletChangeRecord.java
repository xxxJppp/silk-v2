package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.*;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户钱包资金变更流水记录(WalletChangeRecord)表实体类
 *
 * @author yangch
 * @since 2019-06-15 16:40:21
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户钱包资金变更流水记录")
public class WalletChangeRecord {

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id", example = "")
    private Long id;

    /**
     * 账户ID
     */
    @ApiModelProperty(value = "账户ID", example = "")
    private Long walletId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 币种ID
     */
    @ApiModelProperty(value = "币种ID", example = "")
    private String coinId;

    /**
     * 操作类型：变更、回滚
     */
    @ApiModelProperty(value = "操作类型：变更、回滚", example = "")
    private WalletChangeType changeType;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型", example = "")
    private TransactionType type;

    /**
     * 关联的业务ID
     */
    @ApiModelProperty(value = "关联的业务ID", example = "")
    private String refId;

    /**
     * 变动的可用余额
     */
    @ApiModelProperty(value = "变动的可用余额", example = "")
    private BigDecimal tradeBalance;

    /**
     * 变动的冻结余额
     */
    @ApiModelProperty(value = "变动的冻结余额", example = "")
    private BigDecimal tradeFrozenBalance;

    /**
     * 变动的锁仓余额
     */
    @ApiModelProperty(value = "变动的锁仓余额", example = "")
    private BigDecimal tradeLockBalance;

    /**
     * 变更前的可用余额
     */
    @ApiModelProperty(value = "变更前的可用余额", example = "")
    private BigDecimal beforeBalance;

    /**
     * 变更前的冻结余额
     */
    @ApiModelProperty(value = "变更前的冻结余额", example = "")
    private BigDecimal beforeFrozenBalance;

    /**
     * 变更前的锁仓余额
     */
    @ApiModelProperty(value = "变更前的锁仓余额", example = "")
    private BigDecimal beforeLockBalance;

    /**
     * 流水时间
     */
    @ApiModelProperty(value = "流水时间", example = "")
    private Long createTime;

    /**
     * 状态：0=未处理，1=已处理
     * 状态：是否同步,0=未同步/1=已同步
     */
    @ApiModelProperty(value = "状态：0=未处理，1=已处理", example = "")
    private BooleanEnum status;

    /**
     * 变更流水延迟同步到账户的状态:0=实时同步（默认），1=已同步，2=待同步，3=同步中
     */
///    private WalletSyncStatus syncStatus;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名", example = "")
    private String signature;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String comment;

    /**
     * tcc状态：0=none，1=try，2=confirm，3=cancel
     */
    @ApiModelProperty(value = "tcc状态：0=none，1=try，2=confirm，3=cancel", example = "")
    private TccStatus tccStatus;

    /**
     * 必填，币种单位
     */
    @ApiModelProperty(value = "币种单位", example = "")
    private String coinUnit;

    //手续费 信息
    /**
     * 选填，交易手续费
     */
    @ApiModelProperty(value = "交易手续费", example = "")
    private BigDecimal fee = BigDecimal.ZERO;

    /**
     * 选填，优惠手续费（记录交易优惠的手续费）
     */
    @ApiModelProperty(value = "优惠手续费", example = "")
    private BigDecimal feeDiscount = BigDecimal.ZERO;

    /**
     * 选填，实现使用其他币种抵扣手续费:手续费抵扣币种单位（不包括当前币种）
     */
    @ApiModelProperty(value = "手续费抵扣币种单位", example = "")
    private String feeDiscountCoinUnit;

    /**
     * 选填，实现使用其他币种抵扣手续费:抵扣币种对应手续费
     */
    @ApiModelProperty(value = "抵扣币种对应手续费", example = "")
    private BigDecimal feeDiscountAmount = BigDecimal.ZERO;

}