package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 欢乐幸运号活动信息表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LuckyNumberManager implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String name;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;

    /**
     * 活动开奖时间
     */
    @ApiModelProperty(value = "活动开奖时间")
    private Date luckyTime;

    /**
     * 活动币种
     */
    @ApiModelProperty(value = "活动币种")
    private String unit;

    /**
     * 活动票面价
     */
    @ApiModelProperty(value = "活动票面价")
    private BigDecimal amount;

    /**
     * 单人最大票数
     */
    @ApiModelProperty(value = "单人最大票数")
    private Integer singleMaxNum;

    /**
     * 平台手续费 0-100之间2位小数浮点数
     */
    @ApiModelProperty(value = "平台手续费 0-100之间2位小数浮点数")
    private BigDecimal serviceCharge;

    /**
     * 是否隐藏  {0:否(不隐藏) 1:是(隐藏)}
     */
    @ApiModelProperty(value = "是否隐藏")
    private BooleanEnum hidden;

    /**
     * 最小开奖票数
     */
    @ApiModelProperty(value = "最小开奖票数")
    private Integer minTicketNum;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "创建时间")
    private Long createId;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "创建人")
    private Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private Long updateId;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 活动类型{0:幸运号,1:小牛快跑}
     */
    @ApiModelProperty(value = " 活动类型{0:幸运号,1:小牛快跑}")
    private Integer actType;

    /**
     * 是否结算
     */
    @ApiModelProperty(value = "是否结算")
    private BooleanEnum isSettlement;

    /**
     * 参与总人数（活动结束后更新，活动期间在缓存中获取） redis
     */
    @ApiModelProperty(value = "参与总人数")
    private Integer joinMemberCount;

    /**
     * 参与总金额
     */
    @ApiModelProperty(value = "参与总金额")
    private BigDecimal joinMemberAmount;

    /**
     * 购买总票数 redis
     */
    @ApiModelProperty(value = "购买总票数")
    private Integer joinTicketCount;

    /**
     * 中奖票数
     */
    @ApiModelProperty(value = "中奖票数")
    private Integer winNum;

    /**
     * 中奖票号（多票号 英文半角逗号分隔）
     */
    @ApiModelProperty(value = "中奖票号（多票号 英文半角逗号分隔）")
    private String winTickets;

    /**
     * 中奖总人数
     */
    @ApiModelProperty(value = "中奖总人数")
    private Integer winMemberCount;

    /**
     * 平台收益
     */
    @ApiModelProperty(value = "平台收益")
    private BigDecimal platformProfit;

    /**
     * 删除状态0正常1删除
     */
    @ApiModelProperty(value = "删除状态0正常1删除")
    private BooleanEnum deleteState;


}
