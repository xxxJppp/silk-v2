package com.spark.bitrade.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.ReleaseTaskStatus;
import com.spark.bitrade.constant.ReleaseTaskType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 币币交易释放-释放任务表(ExchangeReleaseTask)表实体类
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易释放-释放任务表")
public class ExchangeReleaseTask {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

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
     * 类型：0=锁仓释放，1=冻结释放
     */
    @ApiModelProperty(value = "类型：0=锁仓释放，1=冻结释放", example = "")
    private ReleaseTaskType type;

    /**
     * 释放数量
     */
    @ApiModelProperty(value = "释放数量", example = "")
    private BigDecimal amount;

    /**
     * 关联的账户流水ID
     */
    @ApiModelProperty(value = "关联的账户流水ID", example = "")
    private String refId;

    /**
     * 释放状态：0=未释放,1=已释放
     */
    @ApiModelProperty(value = "释放状态：0=未释放,1=已释放", example = "")
    private ReleaseTaskStatus releaseStatus;

    /**
     * 释放时间
     */
    @ApiModelProperty(value = "释放时间", example = "")
    private Date releaseTime;

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

    public static final String RELEASE_STATUS = "release_status";
    public static final String RELEASE_TIME = "release_time";

}