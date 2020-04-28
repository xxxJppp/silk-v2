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
 * 币币交易释放-锁仓释放总数表(ExchangeReleaseWallet)表实体类
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易释放-锁仓释放总数表")
public class ExchangeReleaseWallet {

    /**
     * ID, memberId:coinSymbol
     */
    @TableId
    @ApiModelProperty(value = "ID, memberId:coinSymbol", example = "")
    private String id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称", example = "")
    private String coinSymbol;

    /**
     * 待释放数量
     */
    @ApiModelProperty(value = "待释放数量", example = "")
    private BigDecimal lockAmount;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @TableField(value="create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新日期
     */
    @ApiModelProperty(value = "更新日期", example = "")
    @TableField(value="update_time", fill = FieldFill.INSERT_UPDATE, update="NOW()")
    private Date updateTime;

    public static final String ID = "id";

}