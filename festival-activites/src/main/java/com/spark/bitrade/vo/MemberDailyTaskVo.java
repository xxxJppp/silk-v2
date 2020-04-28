package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户任务列表
 *
 * @author: Zhong Jiang
 * @date: 2020-01-02 10:11
 */
@Data
public class MemberDailyTaskVo {
    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 推荐好友注册{0:未完成:1已完成}
     */
    @ApiModelProperty(value = "推荐好友注册{0:未完成:1已完成}")
    private Integer taskRegistStatus;

    /**
     * 每日首次登录{0:未完成:1已完成}
     */
    @ApiModelProperty(value = "每日首次登录{0:未完成:1已完成}")
    private Integer taskLoginStatus;

    /**
     * 每日首次币币交易{0:未完成:1已完成}
     */
    @ApiModelProperty(value = "每日首次币币交易{0:未完成:1已完成}")
    private Integer taskExchangeStatus;

    /**
     * 每日首次充币{0:未完成:1已完成}
     */
    @ApiModelProperty(value = "每日首次充币{0:未完成:1已完成}")
    private Integer taskRechargeStatus;

    /**
     * 每日首次法币交易买入成交{0:未完成:1已完成}
     */
    @ApiModelProperty(value = "每日首次法币交易买入成交{0:未完成:1已完成}")
    private Integer taskOtcStatus;

    /**
     * 每日挂1次币币交易买单超过10分钟{0:未完成:1已完成}
     */
    @ApiModelProperty(value = "每日挂1次币币交易买单超过10分钟{0:未完成:1已完成}")
    private Integer taskPutStatus;

    @ApiModelProperty(value = "推荐好友注册累计完成次数")
    private Integer totalTaskRegist;

    @ApiModelProperty(value = "每日首次登录计完成次数")
    private Integer totalTaskLogin;

    @ApiModelProperty(value = "每日首次币币交易完成次数")
    private Integer totalTaskExchange;

    @ApiModelProperty(value = "每日首次充币完成次数")
    private Integer totalTaskRecharge;

    @ApiModelProperty(value = "每日首次法币交易买入成交完成次数")
    private Integer totalTaskOtc;

    @ApiModelProperty(value = "每日挂1次币币交易买单超过10分钟完成次数")
    private Integer totalTaskPut;

    @ApiModelProperty(value = "任务列表时间")
    private Date dailyTaskTime;

}
