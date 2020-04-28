package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 付款匹配记录(SilkPayMatchRecord)表实体类
 *
 * @author wsy
 * @since 2019-07-18 10:36:05
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "付款匹配记录")
public class SilkPayMatchRecord {

    /**
     * 编号
     */
    @TableId
    @ApiModelProperty(value = "编号", example = "")
    private Long id;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号", example = "")
    private Long orderSn;

    /**
     * 匹配账号
     */
    @ApiModelProperty(value = "匹配账号", example = "")
    private Long matchAccount;

    /**
     * 付款方式
     */
    @ApiModelProperty(value = "付款方式", example = "")
    private Integer paymentType;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额", example = "")
    private BigDecimal orderMoney;

    /**
     * 付款金额
     */
    @ApiModelProperty(value = "付款金额", example = "")
    private BigDecimal paymentMoney;

    /**
     * 付款备注
     */
    @ApiModelProperty(value = "付款备注", example = "")
    private String paymentNote;

    /**
     * 付款流水：微信支付宝的流水号
     */
    @ApiModelProperty(value = "付款流水：微信支付宝的流水号", example = "")
    private String paymentOrderNo;

    /**
     * 付款状态：0-下发中 1-付款中 2-付款成功 9-付款失败
     */
    @ApiModelProperty(value = "付款状态：0-下发中 1-付款中 2-付款成功 9-付款失败", example = "")
    private Integer state;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @TableField(value="create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @TableField(value="update_time", fill = FieldFill.INSERT_UPDATE, update="NOW()")
    private Date updateTime;


}