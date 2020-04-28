package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 支付订单(SilkPayOrder)表实体类
 *
 * @author wsy
 * @since 2019-08-23 10:32:56
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "支付订单")
public class SilkPayOrder {

    /**
     * 订单编号
     */
    @TableId
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
     * 收益币数量
     */
    @ApiModelProperty(value = "收益币数量", example = "")
    private BigDecimal profitAmount;

    /**
     * 费率下调系数
     */
    @ApiModelProperty(value = "费率下调系数", example = "")
    private BigDecimal rateReductionFactor;

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
     */
    @ApiModelProperty(value = "收款二维码", example = "")
    private String receiptQrCode;

    /**
     * 付款备注
     */
    @ApiModelProperty(value = "付款备注", example = "")
    private String paymentNote;

    /**
     * 付款位置
     */
    @ApiModelProperty(value = "付款位置", example = "")
    private String paymentLocation;

    /**
     * 订单状态：0-创建 1-匹配成功 2-付款中 3-付款成功 9-付款失败
     */
    @ApiModelProperty(value = "订单状态：0-创建 1-匹配成功 2-付款中 3-付款成功 9-付款失败", example = "")
    private Integer state;

    /**
     * 处理类型：0-自动处理 1-人工处理
     */
    @ApiModelProperty(value = "处理类型：0-自动处理 1-人工处理", example = "")
    private Integer handleType;

    /**
     * 订单描述
     */
    @ApiModelProperty(value = "订单描述", example = "")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 付款时间
     */
    @ApiModelProperty(value = "付款时间", example = "")
    private Date payTime;

    /**
     * 关闭时间
     */
    @ApiModelProperty(value = "关闭时间", example = "")
    private Date closeTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;


}