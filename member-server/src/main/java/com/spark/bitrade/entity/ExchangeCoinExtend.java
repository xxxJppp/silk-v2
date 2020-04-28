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
import lombok.experimental.Accessors;

/**
 * <p>
 * 币币交易-交易对扩展表
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExchangeCoinExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对")
    private String symbol;

    /**
     * 买入吃单手续费折扣率
     */
    @ApiModelProperty(value = "买入吃单手续费折扣率")
    private BigDecimal buyFeeDiscount = BigDecimal.ZERO;

    /**
     * 卖出吃单手续费费率
     */
    @ApiModelProperty(value = "卖出吃单手续费费率")
    private BigDecimal sellFeeDiscount = BigDecimal.ZERO;

    /**
     * 挂单买入手续费费率
     */
    @ApiModelProperty(value = "挂单买入手续费费率")
    private BigDecimal entrustBuyDiscount = BigDecimal.ZERO;

    /**
     * 挂单卖出手续费费率
     */
    @ApiModelProperty(value = "挂单卖出手续费费率")
    private BigDecimal entrustSellDiscount = BigDecimal.ZERO;

    private Date createTime;

    private Date updateTime;

    public static String SYMBOL = "symbol";
}
