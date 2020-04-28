package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * USDC兑换记录(ExchangeUsdcOrder)实体类
 *
 * @author makejava
 * @since 2020-04-08 16:01:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExchangeUsdcOrder implements Serializable {
    private static final long serialVersionUID = -77637470627445246L;
    /**
    * id
    */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
    * 订单号
    */
    private String orderSn;
    /**
    * 兑换人id
    */
    private Long memberId;
    /**
    * 兑换人手机号
    */
    private String phone;
    /**
    * 兑换币种
    */
    private String coinUnit;
    /**
    * 兑换报价币种
    */
    private String exchangeCoinUnit;
    /**
    * 兑换币种数量
    */
    private BigDecimal amount;
    /**
    * 报价币种数量
    */
    private BigDecimal exchangeAmount;
    /**
    * usdc优惠数量,discountUsdc=10：表示有10个USDC不参与报价
    */
    private BigDecimal discountUsdc;
    /**
    * 兑换币种ID
    */
    private Long coinId;
    /**
    * 兑换报价币种ID
    */
    private Long exchangeCoinId;
    /**
    * 兑换时间
    */
    private Date exchangeTime;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;


}