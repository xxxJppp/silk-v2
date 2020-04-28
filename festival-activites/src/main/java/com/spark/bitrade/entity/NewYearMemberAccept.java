package com.spark.bitrade.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户领奖
 * </p>
 *
 * @author qiliao
 * @since 2020-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearMemberAccept implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 令牌id
     */
    private String token;

    /**
     * 币种
     */
    private String coinUnit;

    /**
     * 已发放数量
     */
    private BigDecimal sendedAmount;

    /**
     * 锁仓数量
     */
    private BigDecimal lockAmount;

    /**
     * 已释放数量
     */
    private BigDecimal releasedAmount;

    /**
     * 单次最大释放数量
     */
    private BigDecimal everyMaxReleasedAmount;

    /**
     * 总额数量
     */
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
