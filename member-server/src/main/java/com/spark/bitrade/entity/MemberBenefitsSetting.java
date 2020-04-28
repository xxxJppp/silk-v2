package com.spark.bitrade.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员权益表
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString(includeFieldNames=true)
public class MemberBenefitsSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer levelId;

    /**
     * 吃买单折扣率
     */
    @ApiModelProperty(value = "吃买单折扣率")
    private BigDecimal buyDiscount;

    /**
     * 吃卖单折扣率
     */
    @ApiModelProperty(value = "吃卖单折扣率")
    private BigDecimal sellDiscount;

    /**
     * 挂买单折扣率
     */
    @ApiModelProperty(value = "挂买单折扣率")
    private BigDecimal entrustBuyDiscount;

    /**
     * 挂卖单折扣率
     */
    @ApiModelProperty(value = "挂卖单折扣率")
    private BigDecimal entrustSellDiscount;

    /**
     * 推荐的人以购买方式成为vip1-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以购买方式成为vip1-返佣比例")
    private BigDecimal vip1BuyDiscount;

    /**
     * 推荐的人以锁仓方式成为vip1-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以锁仓方式成为vip1-返佣比例")
    private BigDecimal vip1LockDiscount;

    /**
     * 推荐的人以购买方式成为vip2-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以购买方式成为vip2-返佣比例")
    private BigDecimal vip2BuyDiscount;

    /**
     * 推荐的人以锁仓方式成为vip2-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以锁仓方式成为vip2-返佣比例")
    private BigDecimal vip2LockDiscount;

    /**
     * 推荐的人以购买方式成为vip3-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以购买方式成为vip3-返佣比例")
    private BigDecimal vip3BuyDiscount;

    /**
     * 推荐的人以锁仓方式成为vip3-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以锁仓方式成为vip3-返佣比例")
    private BigDecimal vip3LockDiscount;

    /**
     * 推荐的人以购买方式成为经纪人-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以购买方式成为经纪人-返佣比例")
    private BigDecimal agentBuyDiscount;

    /**
     * 推荐的人以锁仓方式成为经纪人-返佣比例
     */
    @ApiModelProperty(value = "推荐的人以锁仓方式成为经纪人-返佣比例")
    private BigDecimal agentLockDiscount;
    
    /**
     * 	
     */
    @ApiModelProperty(value = "直推会员法币交易法币ID")
    private Long currencyId = 0L;
    /**
     * 	
     */
    @ApiModelProperty(value = "直推会员法币交易返佣比例")
    private BigDecimal currencyDiscount;
    /**
     * 	
     */
    @ApiModelProperty(value = "直推商家法币交易返佣比例")
    private BigDecimal currencyBusinessDiscount;


    private Date createTime;

    private Date updateTime;

}
