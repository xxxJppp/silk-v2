package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanStringEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户安全权限表实体类
 *
 * @author wsy
 * @since 2019-06-14 14:16:08
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class MemberSecuritySet {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 是否开启google认证
     */
    @ApiModelProperty(value = "是否开启google认证", example = "")
    private String isOpenGoogle;

    /**
     * 是否开启手机认证
     */
    @ApiModelProperty(value = "是否开启手机认证", example = "")
    private String isOpenPhone;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 是否开启google登录认证
     */
    @ApiModelProperty(value = "是否开启google登录认证", example = "")
    private BooleanStringEnum isOpenGoogleLogin = BooleanStringEnum.IS_FALSE;

    /**
     * 是否开启google提币认证
     */
    @ApiModelProperty(value = "是否开启google提币认证", example = "")
    private BooleanStringEnum isOpenGoogleUpCoin = BooleanStringEnum.IS_FALSE;

    /**
     * 是否开启手机登录认证
     */
    @ApiModelProperty(value = "是否开启手机登录认证", example = "")
    private BooleanStringEnum isOpenPhoneLogin = BooleanStringEnum.IS_FALSE;

    /**
     * 是否开启手机提币认证
     */
    @ApiModelProperty(value = "是否开启手机提币认证", example = "")
    private BooleanStringEnum isOpenPhoneUpCoin = BooleanStringEnum.IS_FALSE;

    /**
     * 是否开启总资产显示
     */
    @ApiModelProperty(value = "是否开启总资产显示", example = "")
    private BooleanStringEnum isOpenPropertyShow = BooleanStringEnum.IS_FALSE;


    /**
     * 是否开启场外买入交易 0关闭 1开启
     */
    @ApiModelProperty(value = "是否开启场外买入交易", example = "")
    private BooleanStringEnum isOpenExPitTransaction = BooleanStringEnum.IS_TRUE;

    /**
     * 是否开启场外卖出交易 0关闭 1开启
     */
    @ApiModelProperty(value = "是否开启场外卖出交易", example = "")
    private BooleanStringEnum isOpenExPitSellTransaction = BooleanStringEnum.IS_TRUE;

    /**
     * 是否开启BB交易 0关闭 1开启
     */
    @ApiModelProperty(value = "是否开启BB交易", example = "")
    private BooleanStringEnum isOpenBbTransaction = BooleanStringEnum.IS_TRUE;

    /**
     * 是否开启提币 0关闭 1开启
     */
    @ApiModelProperty(value = "是否开启提币", example = "")
    private BooleanStringEnum isOpenUpCoinTransaction = BooleanStringEnum.IS_TRUE;

    /**
     * 是否开启平台内部转账 0关闭 1开启
     */
    @ApiModelProperty(value = "是否开启平台内部转账", example = "")
    private BooleanStringEnum isOpenPlatformTransaction = BooleanStringEnum.IS_TRUE;
}