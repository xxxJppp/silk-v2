package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币交易释放-锁仓明细表(ExchangeReleaseLockRecord)表实体类
 *
 * @author yangch
 * @since 2019-12-16 14:52:15
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易释放-锁仓明细表")
public class ExchangeReleaseLockRecord {

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
     * 锁仓数量
     */
    @ApiModelProperty(value = "锁仓数量", example = "")
    private BigDecimal amount;

    /**
     * 关联的充值流水ID
     */
    @ApiModelProperty(value = "关联的充值流水ID", example = "")
    private String refId;

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


    public static final String REF_ID = "ref_id";
}