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
 * ioco钱包交易记录(IocoMemberTransaction)表实体类
 *
 * @author daring5920
 * @since 2019-07-03 14:45:20
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "ioco钱包交易记录")
public class IocoMemberTransaction {

    /**
     * 记录id
     */
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "记录id", example = "")
    private Long id;

    /**
     * 发起用户
     */
    @ApiModelProperty(value = "发起用户", example = "")
    private Long fromMemberId;

    /**
     * 接收用户
     */
    @ApiModelProperty(value = "接收用户", example = "")
    private Long toMemberId;

    /**
     * 发起币种
     */
    @ApiModelProperty(value = "发起币种", example = "")
    private String fromUnit;

    /**
     * 发起金额
     */
    @ApiModelProperty(value = "发起金额", example = "")
    private BigDecimal fromAmount;

    /**
     * 接收币种
     */
    @ApiModelProperty(value = "接收币种", example = "")
    private String toUnit;

    /**
     * 接收金额
     */
    @ApiModelProperty(value = "接收金额", example = "")
    private BigDecimal toAmount;

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

    /**
     * 对应的申购规则id
     */
    @ApiModelProperty(value = "对应的申购规则id", example = "")
    private Long ruleId;

    /**
     * 0 申购，1是转赠
     */
    @ApiModelProperty(value = "0 申购，1是转赠", example = "")
    private Integer type;


}