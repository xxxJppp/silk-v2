package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2020-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Coin implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "name", type = IdType.AUTO)
    private String name;

    private Integer canAutoWithdraw;

    private Integer canRecharge;

    private Integer canTransfer;

    private Integer canWithdraw;

    private Double cnyRate;

    private Integer enableRpc;

    private Integer isPlatformCoin;

    private Double maxTxFee;

    /**
     * 最大提币数量
     */
    private BigDecimal maxWithdrawAmount;

    private Double minTxFee;

    /**
     * 最小提币数量
     */
    private BigDecimal minWithdrawAmount;

    private String nameCn;

    private Integer sort;

    private Integer status;

    private String unit;

    private Double usdRate;

    /**
     * 提现阈值
     */
    private BigDecimal withdrawThreshold;

    private Boolean hasLegal;

    private String coldWalletAddress;

    /**
     * 矿工费
     */
    private BigDecimal minerFee;

    private String baseCoinUnit;

    /**
     * 提币精度
     */
    private Integer withdrawScale;

    private String exploreUrl;

    /**
     * 手续费率
     */
    private BigDecimal feeRate;

    /**
     * 手续费模式1，定制，2比例
     */
    private Integer feeType;

    /**
     * 是否具有标签 0 有，1无，默认1
     */
    private Integer hasLabel;

    /**
     * 最小到账金额
     */
    private Double minDepositAmount;

    private String content;

    /**
     * 抵扣数值，手续费抵扣模式为0时，代表具体数量，抵扣模式为1时，代表折扣0.8代表8折
     */
    private BigDecimal feeDiscountAmount;

    /**
     * 手续费抵扣币种单位（不包括当前币种）
     */
    private String feeDiscountCoinUnit;

    /**
     * 手续费抵扣模式
     */
    private Integer feeDiscountType;

    /**
     * 更多详情
     */
    private String moreLink;


    /**
     * 自动次数
     */
    private Integer autoCount;



}
