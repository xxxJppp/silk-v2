package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户币币账户重置记录(ExchangeWalletResetRecord)表实体类
 *
 * @author yangch
 * @since 2019-11-21 11:13:06
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户币币账户重置记录")
public class ExchangeWalletResetRecord {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 格式=MemberId:CoinUnit
     */
    @ApiModelProperty(value = "格式=MemberId:CoinUnit", example = "")
    private String walletId;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String coinUnit;

    /**
     * 重置前的余额
     */
    @ApiModelProperty(value = "重置前的余额", example = "")
    private BigDecimal balance;

    /**
     * 重置前的冻结余额
     */
    @ApiModelProperty(value = "重置前的冻结余额", example = "")
    private BigDecimal frozenBalance;

    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息", example = "")
    private String remark;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;


}