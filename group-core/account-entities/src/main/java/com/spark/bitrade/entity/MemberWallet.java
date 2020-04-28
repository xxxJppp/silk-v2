package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 会员钱包表实体类
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "会员钱包")
public class MemberWallet {

    /**
     * 钱包ID
     */
    @TableId
    @ApiModelProperty(value = "钱包ID", example = "")
    private Long id;

    /**
     * 钱包地址
     */
    @ApiModelProperty(value = "钱包地址", example = "")
    private String address;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额", example = "")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "冻结余额", example = "")
    private BigDecimal frozenBalance;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 乐观锁版本号
     */
    @ApiModelProperty(value = "乐观锁版本号", example = "")
    private Integer version;

    /**
     * 币种ID
     */
    @ApiModelProperty(value = "币种ID", example = "")
    private String coinId;

    /**
     * 钱包是否锁定，0否，1是
     */
    @ApiModelProperty(value = "钱包是否锁定，0否，1是", example = "")
    private BooleanEnum isLock;

    /**
     * 锁仓余额
     */
    @ApiModelProperty(value = "锁仓余额", example = "")
    private BigDecimal lockBalance;

    /**
     * 启动充值，0=禁用/1=启用
     */
    @ApiModelProperty(value = "启动充值，0=禁用/1=启用", example = "")
    private BooleanEnum enabledIn;

    /**
     * 启动提币，0=禁用/1=启用
     */
    @ApiModelProperty(value = "启动提币，0=禁用/1=启用", example = "")
    private BooleanEnum enabledOut;


}