package com.spark.bitrade.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 年终集矿石活动配置表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearConfig implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 矿石投放开始时间
     */
    private Date mineralStartTime;

    /**
     * 矿石投放结束时间
     */
    private Date mineralEndTime;

    /**
     * 产出矿石场景
     */
    private String productInfo;

    /**
     * 抽奖开始时间
     */
    private Date luckyStartTime;

    /**
     * 抽奖结束时间
     */
    private Date luckyEndTime;



    /**
     * 每天最多释放比例
     */
    private BigDecimal everydaysMaxRelease;

    /**
     * 活动状态 (0: 有效; 1：无效)
     */
    private Integer activityStatus;

    /**
     * 活动规则
     */
    private String actRules;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private Long updateId;


}
