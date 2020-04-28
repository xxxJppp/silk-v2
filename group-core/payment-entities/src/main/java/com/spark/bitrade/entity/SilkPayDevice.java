package com.spark.bitrade.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 支付设备(SilkPayDevice)表实体类
 *
 * @author wsy
 * @since 2019-08-21 17:42:44
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "支付设备")
public class SilkPayDevice {

    /**
     * 设备编号
     */
    @TableId
    @ApiModelProperty(value = "设备编号", example = "")
    private Long id;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码", example = "")
    private String deviceCode;

    /**
     * 登录密码
     */
    @ApiModelProperty(value = "登录密码", example = "")
    private String devicePwd;

    /**
     * 设备备注
     */
    @ApiModelProperty(value = "设备备注", example = "")
    private String deviceNote;

    /**
     * 在线状态 0-离线 1-在线
     */
    @ApiModelProperty(value = "在线状态 0-离线 1-在线", example = "")
    private BooleanEnum state;

    /**
     * 启用状态 0-停用 1-启用
     */
    @ApiModelProperty(value = "启用状态 0-停用 1-启用", example = "")
    private BooleanEnum enabled;

    /**
     * 内网IP
     */
    @ApiModelProperty(value = "内网IP", example = "")
    private String ip;

    /**
     * 设备标识
     */
    @ApiModelProperty(value = "设备标识", example = "")
    private String imei;

    /**
     * 设备串号
     */
    @ApiModelProperty(value = "设备串号", example = "")
    private String serialNo;

    /**
     * 安卓编号
     */
    @ApiModelProperty(value = "安卓编号", example = "")
    private String androidId;

    /**
     * MAC地址
     */
    @ApiModelProperty(value = "MAC地址", example = "")
    private String mac;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobile;

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