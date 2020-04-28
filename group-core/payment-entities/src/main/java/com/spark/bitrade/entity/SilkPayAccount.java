package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 支付账号(SilkPayAccount)表实体类
 *
 * @author wsy
 * @since 2019-08-23 10:51:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "支付账号")
public class SilkPayAccount {

    /**
     * 账号编号
     */
    @TableId
    @ApiModelProperty(value = "账号编号", example = "")
    private Long id;

    /**
     * 登录账号
     */
    @ApiModelProperty(value = "登录账号", example = "")
    private String username;

    /**
     * 登录密码
     */
    @ApiModelProperty(value = "登录密码", example = "")
    private String password;

    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码", example = "")
    private String payPwd;

    /**
     * 账号昵称
     */
    @ApiModelProperty(value = "账号昵称", example = "")
    private String nick;

    /**
     * 账号类型 0-微信 1-支付宝
     */
    @ApiModelProperty(value = "账号类型 0-微信 1-支付宝", example = "")
    private Integer type;

    /**
     * 账号状态 0-异常 1-正常
     */
    @ApiModelProperty(value = "账号状态 0-异常 1-正常", example = "")
    private BooleanEnum state;

    /**
     * 启用状态 0-停用 1-启用
     */
    @ApiModelProperty(value = "启用状态 0-停用 1-启用", example = "")
    private BooleanEnum enabled;

    /**
     * 位置精度
     */
    @ApiModelProperty(value = "位置精度", example = "")
    private Float siteLon;

    /**
     * 位置维度
     */
    @ApiModelProperty(value = "位置维度", example = "")
    private Float siteLat;

    /**
     * 状态描述
     */
    @ApiModelProperty(value = "状态描述", example = "")
    private String stateRemark;

    /**
     * 关联设备
     */
    @ApiModelProperty(value = "关联设备", example = "")
    private Long deviceId;

    /**
     * 可付总额
     */
    @ApiModelProperty(value = "可付总额", example = "")
    private BigDecimal totalUsable;

    /**
     * 已付总额
     */
    @ApiModelProperty(value = "已付总额", example = "")
    private BigDecimal totalAlready;

    /**
     * 可付次数
     */
    @ApiModelProperty(value = "可付次数", example = "")
    private Integer numberUsable;

    /**
     * 已付次数
     */
    @ApiModelProperty(value = "已付次数", example = "")
    private Integer numberAlready;

    /**
     * 每日限额
     */
    @ApiModelProperty(value = "每日限额", example = "")
    private BigDecimal quotaDaily;

    /**
     * 剩余限额
     */
    @ApiModelProperty(value = "剩余限额", example = "")
    private BigDecimal quotaSurplus;

    /**
     * 单笔限额
     */
    @ApiModelProperty(value = "单笔限额", example = "")
    private BigDecimal quotaSingle;

    /**
     * 拆账限额
     */
    @ApiModelProperty(value = "拆账限额", example = "")
    private BigDecimal quotaSplit;

    /**
     * 解限时间
     */
    @ApiModelProperty(value = "解限时间", example = "")
    private Date quotaRelieve;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;


}