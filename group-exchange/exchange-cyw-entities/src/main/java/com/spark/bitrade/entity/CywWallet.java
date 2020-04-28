package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.constants.CywLockStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 机器人钱包(CywWallet)表实体类
 *
 * @author archx
 * @since 2019-09-03 15:45:45
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "机器人钱包")
public class CywWallet {

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
    private CywLockStatus isLock;

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