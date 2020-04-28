package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户WAL流水同步记录表(ExchangeWalletSyncRecord)表实体类
 *
 * @author archx
 * @since 2019-09-03 15:45:29
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "账户WAL流水同步记录表")
public class ExchangeWalletSyncRecord {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 账户ID
     */
    @ApiModelProperty(value = "账户ID", example = "")
    private String walletId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 币种单位
     */
    @ApiModelProperty(value = "币种单位", example = "")
    private String coinUnit;

    /**
     * 总计变动数额
     */
    @ApiModelProperty(value = "总计变动数额", example = "")
    private BigDecimal sumAmount;

    /**
     * 增加的变动数额
     */
    @ApiModelProperty(value = "增加的变动数额", example = "")
    private BigDecimal increasedAmount;

    /**
     * 总计冻结数额
     */
    @ApiModelProperty(value = "总计冻结数额", example = "")
    private BigDecimal sumFrozenAmount;

    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息", example = "")
    private String remark;

    /**
     * 状态：0-进行中，1-已完成
     */
    @ApiModelProperty(value = "状态：0-进行中，1-已完成", example = "")
    private ExchangeProcessStatus status;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新日期
     */
    @ApiModelProperty(value = "更新日期", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, update = "NOW()")
    private Date updateTime;
}