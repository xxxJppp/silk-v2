package com.spark.bitrade.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.PromotionRewardCoin;
import com.spark.bitrade.constant.PromotionRewardCycle;
import com.spark.bitrade.constant.PromotionRewardType;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 推广奖励配置表实体类
 *
 * @author yangch
 * @since 2019-10-31 17:28:01
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "推广奖励配置")
public class RewardPromotionSetting {
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 推广注册：{"one": 1,"two": 0.1}
     * <p>
     * 法币暂定首次推广交易：{"one":  10,"two": 5}交易数量占比,交易数量全部换算成usdt来计算
     * <p>
     * 币币推广交易：{"one":  10,"two": 5,"three":6} 手续费占比
     */
    @ApiModelProperty(value = "手续费占比", example = "")
    private String info;

    /**
     * 启、禁用状态
     */
    @ApiModelProperty(value = "启、禁用状态", example = "")
    private BooleanEnum status;

    /**
     * 返佣类型
     */
    @ApiModelProperty(value = "返佣类型", example = "")
    private PromotionRewardType type;

    @ApiModelProperty(value = "", example = "")
    private Date updateTime;

    @ApiModelProperty(value = "", example = "")
    private Long adminId;

    @ApiModelProperty(value = "", example = "")
    private String coinId;

    /**
     * 生效时间，从注册之日算起。单位天 .主要推广交易用到
     */
    @ApiModelProperty(value = "", example = "")
    private Integer effectiveTime;

    /**
     * 返佣币种（交易币、USDT、SLB）
     */
    @ApiModelProperty(value = "返佣币种（交易币、USDT、SLB）", example = "")
    private PromotionRewardCoin rewardCoin;

    /**
     * 返佣周期（实时、天、周、月）
     */
    @ApiModelProperty(value = "返佣周期（实时、天、周、月）", example = "")
    private PromotionRewardCycle rewardCycle;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String note;

    /**
     * 活动说明
     */
    @ApiModelProperty(value = "活动说明", example = "")
    private String data;

    /**
     * 活动标题
     */
    @ApiModelProperty(value = "活动标题", example = "")
    private String title;

    /**
     * 是否显示到首页
     */
    @ApiModelProperty(value = "是否显示到首页", example = "")
    private Object isFrontShow;

}