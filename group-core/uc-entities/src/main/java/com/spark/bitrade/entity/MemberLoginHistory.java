package com.spark.bitrade.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.LoginType;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (MemberLoginHistory)表实体类
 *
 * @author wsy
 * @since 2019-06-14 14:54:49
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class MemberLoginHistory {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 区域信息
     */
    @ApiModelProperty(value = "区域信息", example = "")
    private String area;

    /**
     * 登录IP
     */
    @ApiModelProperty(value = "登录IP", example = "")
    private String loginip;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间", example = "")
    private Date loginTime;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 登录类型（WEB、android、IOS）
     */
    @ApiModelProperty(value = "登录类型（WEB、android、IOS）", example = "")
    private LoginType type;

    /**
     * 第三方平台标志
     */
    @ApiModelProperty(value = "第三方平台标志", example = "")
    private String thirdMark;

    /**
     * 是否为注册信息，0：表示登录信息;1：表示注册信息
     */
    @ApiModelProperty(value = "是否为注册信息，0：表示登录信息;1：表示注册信息", example = "")
    private BooleanEnum isRegistrate;

    /**
     * 手机是否root或越狱
     */
    @ApiModelProperty(value = "手机是否root或越狱", example = "")
    private BooleanEnum isRootOrJailbreak;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号", example = "")
    private String model;

    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商", example = "")
    private String producers;

    /**
     * 系统版本
     */
    @ApiModelProperty(value = "系统版本", example = "")
    private String systemVersion;

    /**
     * 唯一标志码UUID
     */
    @ApiModelProperty(value = "唯一标志码UUID", example = "")
    private String uuid;


}