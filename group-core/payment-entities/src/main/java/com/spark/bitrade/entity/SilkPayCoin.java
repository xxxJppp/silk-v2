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
 * 币种配置(SilkPayCoin)表实体类
 *
 * @author wsy
 * @since 2019-08-21 14:19:52
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币种配置")
public class SilkPayCoin {

    /**
     * 编号
     */
    @TableId
    @ApiModelProperty(value = "编号", example = "")
    private Long id;

    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称", example = "")
    private String name;

    /**
     * 币种单位
     */
    @ApiModelProperty(value = "币种单位", example = "")
    private String unit;

    /**
     * 小数位精度
     */
    @ApiModelProperty(value = "小数位精度", example = "")
    private Integer scale;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", example = "")
    private Integer state;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 最高单笔交易
     */
    @ApiModelProperty(value = "最高单笔交易", example = "")
    private BigDecimal tradeMax;

    /**
     * 费率下调系数
     */
    @ApiModelProperty(value = "费率下调系数", example = "")
    private BigDecimal rateReductionFactor;

    /**
     * 币种每日最高交易
     */
    @ApiModelProperty(value = "币种每日最高交易", example = "")
    private BigDecimal coinDailyMax;

    /**
     * 单个用户最高交易
     */
    @ApiModelProperty(value = "单个用户最高交易", example = "")
    private BigDecimal userTotalMax;

    /**
     * 最低单笔交易
     */
    @ApiModelProperty(value = "最低单笔交易", example = "")
    private BigDecimal tradeMin;

    /**
     * 用户每日最高交易
     */
    @ApiModelProperty(value = "用户每日最高交易", example = "")
    private BigDecimal userDailyMax;

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