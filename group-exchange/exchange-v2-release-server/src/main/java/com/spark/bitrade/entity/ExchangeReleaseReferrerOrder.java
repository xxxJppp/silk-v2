package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.ReferrerOrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币交易-推荐人闪兑订单表(ExchangeReleaseReferrerOrder)表实体类
 *
 * @author yangch
 * @since 2020-01-17 17:18:13
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易-推荐人闪兑订单表")
public class ExchangeReleaseReferrerOrder {

    /**
     * 订单ID，PK
     */
    @TableId
    @ApiModelProperty(value = "订单ID，PK", example = "")
    private String refOrderId;

    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对", example = "")
    private String refSymbol;

    /**
     * 交易币
     */
    @ApiModelProperty(value = "交易币", example = "")
    private String coinSymbol;

    /**
     * 结算币
     */
    @ApiModelProperty(value = "结算币", example = "")
    private String baseSymbol;

    /**
     * 计划下单数量
     */
    @ApiModelProperty(value = "计划下单数量", example = "")
    private BigDecimal refAmount;

    /**
     * 实际下单数量
     */
    @ApiModelProperty(value = "实际下单数量", example = "")
    private BigDecimal refPlaceAmount;

    /**
     * 被推荐人闪兑冻结币数量
     */
    @ApiModelProperty(value = "被推荐人闪兑冻结币数量", example = "")
    private BigDecimal inviteeFreezeAmount;

    /**
     * 推荐人用户ID
     */
    @ApiModelProperty(value = "推荐人用户ID", example = "")
    private Long inviterId;

    /**
     * 被推荐人用户ID
     */
    @ApiModelProperty(value = "被推荐人用户ID", example = "")
    private Long inviteeId;

    /**
     * 订单状态：0=交易中，1=待完成，2=已完成
     */
    @ApiModelProperty(value = "订单状态：0=交易中，1=待完成，2=已完成", example = "")
    private ReferrerOrderStatus status;

    /**
     * 成交均价
     */
    @ApiModelProperty(value = "成交均价", example = "")
    private BigDecimal rate;

    /**
     * 计划闪兑数量
     */
    @ApiModelProperty(value = "计划闪兑数量", example = "")
    private BigDecimal freezeAmount;

    /**
     * 实际闪兑数量
     */
    @ApiModelProperty(value = "实际闪兑数量", example = "")
    private BigDecimal tradedAmount;

    /**
     * 实际闪兑额
     */
    @ApiModelProperty(value = "实际闪兑额", example = "")
    private BigDecimal tradedTurnover;

    /**
     * 推荐人手续费（基币）
     */
    @ApiModelProperty(value = "推荐人手续费（基币）", example = "")
    private BigDecimal inviterFee;

    /**
     * 被推荐人手续费（交易币）
     */
    @ApiModelProperty(value = "被推荐人手续费（交易币）", example = "")
    private BigDecimal inviteeFee;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新日期
     */
    @ApiModelProperty(value = "更新日期", example = "")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, update = "NOW()")
    private Date updateTime;

}