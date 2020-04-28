package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ioco活动钱包数据(IocoActivityWallet)表实体类
 *
 * @author daring5920
 * @since 2019-07-03 14:45:20
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "ioco活动钱包数据")
public class IocoActivityWallet {

    /**
     * 记录id
     */
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "记录id", example = "")
    private Long id;

    /**
     * 活动
     */
    @ApiModelProperty(value = "活动", example = "")
    private Long ruleId;

    /**
     * 活动
     */
    @ApiModelProperty(value = "活动", example = "")
    private Long activityId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "")
    private Long memberId;

    /**
     * 币种单位（SLP）
     */
    @ApiModelProperty(value = "币种单位（SLP）", example = "")
    private String unit;

    /**
     * 剩余额度
     */
    @ApiModelProperty(value = "剩余额度", example = "")
    private BigDecimal balance;

    /**
     * 计划金额
     */
    @ApiModelProperty(value = "计划金额", example = "")
    private BigDecimal planAmount;

    /**
     * 单次最低申购的数量
     */
    @ApiModelProperty(value = "单次最低申购的数量", example = "")
    private BigDecimal minAmount;

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