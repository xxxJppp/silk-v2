package com.spark.bitrade.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付订单(SilkPayOrder)表实体类
 *
 * @author wsy
 * @since 2019-07-18 10:36:05
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "支付订单")
public class SilkPayOrderListVo {

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号", example = "")
    private Long id;

    /**
     * 用户编号
     */
    @ApiModelProperty(value = "用户编号", example = "")
    private Long memberId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称", example = "")
    private String memberName;

    /**
     * 用户实名
     */
    @ApiModelProperty(value = "用户实名", example = "")
    private String memberRealName;

    /**
     * 交易币种
     */
    @ApiModelProperty(value = "交易币种", example = "")
    private String coinId;

    /**
     * 交易数量
     */
    @ApiModelProperty(value = "交易数量", example = "")
    private BigDecimal amount;

    /**
     * 交易价格
     */
    @ApiModelProperty(value = "交易价格", example = "")
    private BigDecimal dealPrice;

    /**
     * 交易市价
     */
    @ApiModelProperty(value = "交易市价", example = "")
    private BigDecimal marketPrice;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额", example = "")
    private BigDecimal money;

    /**
     * 收款方式 0-微信 1-支付宝
     */
    @ApiModelProperty(value = "收款方式 0-微信 1-支付宝", example = "")
    private Integer receiptType;

    /**
     * 收款人姓名
     */
    @ApiModelProperty(value = "收款人姓名", example = "")
    private String receiptName;

    /**
     * 收款二维码
     *//*
    @ApiModelProperty(value = "收款二维码", example = "")
    private String receiptQrCode;*/


    /**
     * 订单状态：0-创建 1-匹配成功 2-付款中 3-付款成功 9-付款失败
     */
    @ApiModelProperty(value = "订单状态：0-创建 1-匹配成功 2-付款中 3-付款成功 9-付款失败", example = "")
    private Integer state;


    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;


}