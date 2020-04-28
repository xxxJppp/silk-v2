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
 * 全局基础配置(SilkPayGlobalConfig)表实体类
 *
 * @author wsy
 * @since 2019-08-26 16:29:04
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "全局基础配置")
public class SilkPayGlobalConfig {

    /**
     * 编号
     */
    @TableId
    @ApiModelProperty(value = "编号", example = "")
    private Long id;

    /**
     * 用户单笔最低限额：最低支付CNY
     */
    @ApiModelProperty(value = "用户单笔最低限额：最低支付CNY", example = "")
    private BigDecimal userSingleMin;

    /**
     * 用户默认单日最高限额CNY
     */
    @ApiModelProperty(value = "用户默认单日最高限额CNY", example = "")
    private BigDecimal userSingleQuota;

    /**
     * 用户默认总最高限额CNY
     */
    @ApiModelProperty(value = "用户默认总最高限额CNY", example = "")
    private BigDecimal userTotalQuota;

    /**
     * 用户默认总交易次数
     */
    @ApiModelProperty(value = "用户默认总交易次数", example = "")
    private Integer userDailyMax;

    /**
     * 用户总次最高限次
     */
    @ApiModelProperty(value = "用户总次最高限次", example = "")
    private Integer userTotalDaily;

    /**
     * 支付宝付款开关
     */
    @ApiModelProperty(value = "支付宝付款开关", example = "")
    private Integer enabledAliPay;

    /**
     * 微信付款开关
     */
    @ApiModelProperty(value = "微信付款开关", example = "")
    private Integer enabledWxPay;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;


}