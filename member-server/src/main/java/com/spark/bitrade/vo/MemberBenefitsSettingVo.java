package com.spark.bitrade.vo;

import com.spark.bitrade.entity.MemberBenefitsSetting;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.19 13:51
 */
@Data
public class MemberBenefitsSettingVo {

    /**
     * 中文名
     */
    @ApiModelProperty(value = "中文名")
    private String levelNameZh;

    /**
     * 中文繁体
     */
    @ApiModelProperty(value = "中文繁体")
    private String levelNameHk;

    /**
     * 英文名
     */
    @ApiModelProperty(value = "英文名")
    private String levelNameEn;

    /**
     * 韩文名
     */
    @ApiModelProperty(value = "韩文名")
    private String levelNameKo;

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
    
    private Data createTime;

    private Data updateTime;

}
