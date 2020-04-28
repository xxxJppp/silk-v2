package com.spark.bitrade.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 奖励币种
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearCoin implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 奖励币种
     */
    private String coinUnit;

    /**
     * 奖励数量
     */
    private BigDecimal wardAmount;
    /**
     * 已发放数量
     */
    private BigDecimal costAmount;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 奖励锁仓比例
     */
    private BigDecimal lockPercent;

    /**
     * 每次交易释放比例
     */
    private BigDecimal everyReleasePercent;
}
