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
 * 用户支付配置(SilkPayUserConfig)表实体类
 *
 * @author wsy
 * @since 2019-08-21 14:21:28
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户支付配置")
public class SilkPayUserConfig {

    /**
     * 用户ID
     */
    @TableId
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 支付功能按默认值生效
     */
    @ApiModelProperty(value = "支付功能按默认值生效", example = "")
    private Integer defaultConfig;

    /**
     * 开启SilkPay支付功能
     */
    @ApiModelProperty(value = "开启SilkPay支付功能", example = "")
    private Integer enablePay;

    /**
     * 交易限额总额CNY
     */
    @ApiModelProperty(value = "交易限额总额CNY", example = "")
    private BigDecimal quotaTotal;

    /**
     * 每日最高额度CNY
     */
    @ApiModelProperty(value = "每日最高额度CNY", example = "")
    private BigDecimal quotaDaily;

    /**
     * 用户总交易次数
     */
    @ApiModelProperty(value = "用户总交易次数", example = "")
    private Integer limitTotal;

    /**
     * 用户每日最高交易次数
     */
    @ApiModelProperty(value = "用户每日最高交易次数", example = "")
    private Integer limitDaily;

    /**
     * 已交易总次数
     */
    @ApiModelProperty(value = "已交易总次数", example = "")
    private Integer totalNumber;

    /**
     * 今日交易次数
     */
    @ApiModelProperty(value = "今日交易次数", example = "")
    private Integer dailyNumber;

    /**
     * 已交易总额
     */
    @ApiModelProperty(value = "已交易总额", example = "")
    private BigDecimal totalAmount;

    /**
     * 今日交易额
     */
    @ApiModelProperty(value = "今日交易额", example = "")
    private BigDecimal dailyAmount;

    /**
     * 解限时间
     */
    @ApiModelProperty(value = "解限时间", example = "")
    private Date quotaRelieve;

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