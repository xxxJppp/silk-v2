package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币交易释放与冻结-规则配置表(ExchangeReleaseFreezeRule)表实体类
 *
 * @author yangch
 * @since 2019-12-16 14:45:06
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易释放与冻结-规则配置表")
public class ExchangeReleaseFreezeRule {

    /**
     * 交易对
     */
    @TableId
    @ApiModelProperty(value = "交易对", example = "")
    private String symbol;

    /**
     * 启动买入规则：0=关闭，1=启动
     */
    @ApiModelProperty(value = "启动买入规则：0=关闭，1=启动", example = "")
    private BooleanEnum enableBuy;

    /**
     * 启动卖出规则：0=关闭，1=启动
     */
    @ApiModelProperty(value = "启动卖出规则：0=关闭，1=启动", example = "")
    private BooleanEnum enableSell;

    /**
     * 买入成交后冻结时间（单位：小时）
     */
    @ApiModelProperty(value = "买入成交后冻结时间（单位：小时）", example = "")
    private Integer freezeDuration;

    /**
     * 释放规则：买入数量
     */
    @ApiModelProperty(value = "释放规则：买入数量", example = "")
    private BigDecimal rateBuyAmount;

    /**
     * 释放规则：释放数量
     */
    @ApiModelProperty(value = "释放规则：释放数量", example = "")
    private BigDecimal rateReleaseAmount;

    /**
     * 卖出价最小递增价格
     */
    @ApiModelProperty(value = "卖出价最小递增价格", example = "")
    private BigDecimal sellMinIncrement;

    /**
     * 每个卖价最大交易数量
     */
    @ApiModelProperty(value = "每个卖价最大交易数量", example = "")
    private BigDecimal sellMaxTradeAmount;

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