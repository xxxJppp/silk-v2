package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (OtcCoin)实体类
 *
 * @author ss
 * @date 2020-03-19 10:23:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OtcCoin implements Serializable {
    private static final long serialVersionUID = -88470357721617530L;

    private Long id;
    /**
    * 买入广告最低发布数量
    */
    private BigDecimal buyMinAmount;

    private Integer isPlatformCoin;
    /**
    * 交易手续费率
    */
    private BigDecimal jyRate;
    /**
    * 交易手续费率（买币）
    */
    private BigDecimal buyJyRate;

    private String name;

    private String nameCn;
    /**
    * 卖出广告最低发布数量
    */
    private BigDecimal sellMinAmount;

    private Integer sort;

    private Integer status;

    private String unit;
    /**
    * 最高单笔交易额
    */
    private BigDecimal tradeMaxLimit;
    /**
    * 最低单笔交易额
    */
    private BigDecimal tradeMinLimit;
    /**
    * 买币手续费的折扣率
    */
    private BigDecimal feeBuyDiscount;
    /**
    * 卖币手续费的折扣率
    */
    private BigDecimal feeSellDiscount;
    /**
    * 货币小数位精度（默认为8位）
    */
    private Integer coinScale;
    /**
    * 发布购买广告时账户最低可用余额要求（针对普通用户上架）
    */
    private BigDecimal generalBuyMinBalance;
    /**
    * 使用优惠币种结算的精度（针对普通用户上架广告费用，默认为8位）
    */
    private Integer generalDiscountCoinScale;
    /**
    * 可用的支付优惠币种（针对普通用户上架广告费用；使用币种的简写名称，默认为SLU）
    */
    private String generalDiscountCoinUnit;
    /**
    * 支付优惠折扣率(针对普通用户上架广告费用，用小数来表示百分比)
    */
    private BigDecimal generalDiscountRate;
    /**
    * 普通用户上架广告费用
    */
    private BigDecimal generalFee;
    /**
    * 普通用户上架广告费用的币种（使用币种的简写名称，默认为USDT）
    */
    private String generalFeeCoinUnit;
    /**
     * 法币ID
     */
    private String currencyId;


}
