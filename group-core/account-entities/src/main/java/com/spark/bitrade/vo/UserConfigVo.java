package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户支付配置vo
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
@Data
@ApiModel(description = "用户支付配置vo")
public class UserConfigVo {


    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "")
    private Long memberId;

    /**
     * 用户单笔最低限额：最低支付CNY
     */
    @ApiModelProperty(value = "用户单笔最低限额：最低支付CNY", example = "")
    private BigDecimal userSingleMin;
    /**
     * 今日交易额
     */
    @ApiModelProperty(value = "今日交易额", example = "")
    private BigDecimal dailyAmount;

    /**
     * 今日交易次数
     */
    @ApiModelProperty(value = "今日交易次数", example = "")
    private Integer dailyNumber;

    /**
     * 每日最高额度CNY
     */
    @ApiModelProperty(value = "每日最高额度CNY", example = "")
    private BigDecimal quotaDaily;

    /**
     * 用户每日最高交易次数
     */
    @ApiModelProperty(value = "用户每日最高交易次数", example = "")
    private Integer limitDaily;

    /**
     * 用户总交易次数
     */
    @ApiModelProperty(value = "用户总交易次数", example = "")
    private Integer limitTotal;

    /**
     * 已交易总次数
     */
    @ApiModelProperty(value = "已交易总次数", example = "")
    private Integer totalNumber;

    /**
     * 交易限额总额CNY
     */
    @ApiModelProperty(value = "交易限额总额CNY", example = "")
    private BigDecimal quotaTotal;

    /**
     * 已交易总额
     */
    @ApiModelProperty(value = "已交易总额", example = "")
    private BigDecimal totalAmount;
}
