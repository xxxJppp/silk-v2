package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.TradeBehaviorType;
import com.spark.bitrade.dsc.annotation.DscEntity;
import com.spark.bitrade.dsc.annotation.DscIgnore;
import com.spark.bitrade.dsc.annotation.DscStorage;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币账户WAL流水记录表(ExchangeWalletWalRecord)表实体类
 *
 * @author archx
 * @since 2019-09-03 15:45:18
 */
@SuppressWarnings("serial")
@Data
@DscEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币账户WAL流水记录表")
public class ExchangeWalletWalRecord {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 币种单位
     */
    @ApiModelProperty(value = "币种单位", example = "")
    private String coinUnit;

    /**
     * 变动的可用余额
     */
    @ApiModelProperty(value = "变动的可用余额", example = "")
    private BigDecimal tradeBalance;

    /**
     * 变动的冻结余额
     */
    @ApiModelProperty(value = "变动的冻结余额", example = "")
    private BigDecimal tradeFrozen;

    /**
     * 交易手续费
     */
    @ApiModelProperty(value = "交易手续费", example = "")
    private BigDecimal fee;

    /**
     * 优惠手续费
     */
    @ApiModelProperty(value = "优惠手续费", example = "")
    private BigDecimal feeDiscount;

    /**
     * 汇率（USDT）
     */
    @ApiModelProperty(value = "汇率（USDT）", example = "")
    private BigDecimal rate;

    /**
     * 交易类型：0=none...
     */
    @ApiModelProperty(value = "交易类型", example = "")
    private WalTradeType tradeType;

    /**
     * 关联的业务ID
     */
    @ApiModelProperty(value = "关联的业务ID", example = "")
    private String refId;

    /**
     * 同步ID
     */
    @ApiModelProperty(value = "同步ID", example = "")
    private Long syncId;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名", example = "")
    @DscStorage(type = DscStorage.Type.SIGNATURE)
    private String signature;

    /**
     * 状态：0=未处理，1=已处理
     */
    @ApiModelProperty(value = "状态：0=未处理，1=已处理", example = "")
    private ExchangeProcessStatus status;

    /**
     * 订单撮合类型：吃单、挂单(仅仅针对WalTradeType.TURNOVER类型，其他类型为null)
     */
    private TradeBehaviorType orderMatchType = TradeBehaviorType.NULL;

//    /**
//     * tcc状态：0=none，1=try，2=confirm，3=cancel
//     */
//    @ApiModelProperty(value = "tcc状态：0=none，1=try，2=confirm，3=cancel", example = "")
//    private Integer tccStatus;


    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @DscIgnore
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, update = "NOW()")
    @DscIgnore
    private Date updateTime;


}