package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 会员币币交易优惠规则表实体类
 *
 * @author yangch
 * @since 2019-11-06 10:33:31
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "会员币币交易优惠规则表")
public class ExchangeMemberDiscountRule {

    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 任务创建时间
     */
    @ApiModelProperty(value = "任务创建时间", example = "")
    private Date createTime;

    /**
     * 是否启用配置
     */
    @ApiModelProperty(value = "是否启用配置", example = "")
    private BooleanEnum enable;

    /**
     * 吃单：买币手续费的折扣率，用小数表示百分比（表示减免的手续费）。eg：20%=0.2；1为手续费全免，0为不优惠手续费
     */
    @ApiModelProperty(value = "买币手续费的折扣率", example = "")
    private BigDecimal feeBuyDiscount;

    /**
     * 吃单：卖币手续费的折扣率，用小数表示百分比（表示减免的手续费）。eg：20%=0.2；1为手续费全免，0为不优惠手续费
     */
    @ApiModelProperty(value = "卖币手续费的折扣率", example = "")
    private BigDecimal feeSellDiscount;

    /**
     * 挂单：买币手续费的折扣率，用小数表示百分比（表示减免的手续费）。eg：20%=0.2；1为手续费全免，0为不优惠手续费
     */
    @ApiModelProperty(value = "挂单：买币手续费的折扣率", example = "")
    private BigDecimal feeEntrustBuyDiscount;

    /**
     * 挂单：卖币手续费的折扣率，用小数表示百分比（表示减免的手续费）。eg：20%=0.2；1为手续费全免，0为不优惠手续费
     */
    @ApiModelProperty(value = "挂单：卖币手续费的折扣率", example = "")
    private BigDecimal feeEntrustSellDiscount;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String note;

    /**
     * 交易对名称，格式：BTC/USDT
     * 用*号代表所有交易对
     */
    @ApiModelProperty(value = "交易对名称，可以为*", example = "")
    private String symbol;

}