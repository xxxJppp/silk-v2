package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * ioco申购页面对象
 * @author shenzucai
 * @time 2019.07.03 15:55
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "ioco申购页面对象")
@Builder
public class IocoPurchaseVo {

    /**
     * 记录id
     */
    @ApiModelProperty(value = "记录id", example = "")
    private Long id;
    /**
     * 活动期数
     */
    @ApiModelProperty(value = "活动期数", example = "")
    private Integer activityPeriod;

    /**
     * 前台活动url
     */
    @ApiModelProperty(value = "前台活动url", example = "")
    private String activityUrl;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String activityName;

    /**
     * 活动状态0未生效，1生效，2失效
     */
    @ApiModelProperty(value = "活动状态0未生效，1生效，2失效", example = "")
    private Integer status;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间", example = "")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @ApiModelProperty(value = "活动结束时间", example = "")
    private Date endTime;

    /**
     * 每份对应usdt数量 用于申购规则
     */
    @ApiModelProperty(value = "每份对应usdt数量 用于申购规则", example = "")
    private BigDecimal usdtAmount;

    /**
     * 每份对应bt数量 用于申购规则
     */
    @ApiModelProperty(value = "每份对应bt数量 用于申购规则", example = "")
    private BigDecimal btAmount;

    /**
     * 每份对应slp数量 用于申购规则
     */
    @ApiModelProperty(value = "每份对应slp数量 用于申购规则", example = "")
    private BigDecimal slpAmount;

    /**
     * 该活动总的slp申购数量
     */
    @ApiModelProperty(value = "该活动总的slp申购数量", example = "")
    private BigDecimal activitTotalSlpAmount;

    /**
     * 该活动总的slp申购剩余数量
     */
    @ApiModelProperty(value = "该活动总的slp申购剩余数量", example = "")
    private BigDecimal activitTotalSlpBalance;

    /**
     * 直推人数
     */
    @ApiModelProperty(value = "直推人数", example = "")
    private Integer directMembers;

    /**
     * 推荐总人数
     */
    @ApiModelProperty(value = "推荐总人数", example = "")
    private Integer allMembers;

    /**
     * slp可用余额
     */
    @ApiModelProperty(value = "slp可用余额", example = "")
    private BigDecimal balance;

    /**
     * 可申购SLP数量
     */
    @ApiModelProperty(value = "可申购SLP数量", example = "")
    private BigDecimal planAmount;

    /**
     * 剩余可申购SLP数量
     */
    @ApiModelProperty(value = "剩余可申购SLP数量", example = "")
    private BigDecimal remainAmount;

    /**
     * 单次最低申购SLP数量
     */
    @ApiModelProperty(value = "单次最低申购SLP数量", example = "")
    private BigDecimal minSlpAmount;

    /**
     * 单次最低申购SLP份数
     */
    @ApiModelProperty(value = "单次最低申购SLP份数", example = "")
    private Integer minShare;

    /**
     * 兑换币种及其可用余额列表
     */
    @ApiModelProperty(value = "兑换币种及其可用余额列表", example = "")
    private List<MemberBalance> memberBalances;
}
