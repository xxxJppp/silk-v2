package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.dsc.annotation.DscEntity;
import com.spark.bitrade.dsc.annotation.DscIgnore;
import com.spark.bitrade.dsc.annotation.DscStorage;
import com.spark.bitrade.entity.constants.ExchangeLockStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币钱包(ExchangeWallet)表实体类
 *
 * @author archx
 * @since 2019-09-03 15:45:45
 */
@SuppressWarnings("serial")
@Data
@DscEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易钱包")
public class ExchangeWallet {

    /**
     * ID, MemberId:CoinUnit
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private String id;

    /**
     * 钱包地址
     */
    @ApiModelProperty(value = "钱包地址", example = "")
    private String address;

    /**
     * 余额
     */
    @ApiModelProperty(value = "余额", example = "")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "冻结余额", example = "")
    private BigDecimal frozenBalance;

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
     * 是否锁定
     */
    @ApiModelProperty(value = "是否锁定", example = "")
    @DscIgnore
    private ExchangeLockStatus isLock;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名", example = "")
    @DscStorage(type = DscStorage.Type.SIGNATURE)
    private String signature;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @DscIgnore
    private Date createTime;

    /**
     * 更新日期
     */
    @ApiModelProperty(value = "更新日期", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, update = "NOW()")
    @DscIgnore
    private Date updateTime;


}