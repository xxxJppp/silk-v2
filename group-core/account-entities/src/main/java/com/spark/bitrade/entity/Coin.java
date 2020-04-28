package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.CoinFeeType;
import com.spark.bitrade.constant.CommonStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 币种(Coin)表实体类
 *
 * @author zhaYanjun
 * @since 2019-06-20 17:37:10
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币种")
public class Coin {

    /**
     * 币种名称
     */
    @TableId
    @ApiModelProperty(value = "币种名称", example = "")
    private String name;

    /**
     * 是否能自动提币
     */
    @ApiModelProperty(value = "是否能自动提币", example = "")
    private BooleanEnum canAutoWithdraw;

    /**
     * 是否能充币
     */
    @ApiModelProperty(value = "是否能充币", example = "")
    private BooleanEnum canRecharge;

    /**
     * 是否能转账
     */
    @ApiModelProperty(value = "是否能转账", example = "")
    private BooleanEnum canTransfer = BooleanEnum.IS_TRUE;

    /**
     * 是否能提币
     */
    @ApiModelProperty(value = "是否能提币", example = "")
    private BooleanEnum canWithdraw;

    /**
     * 对人民币汇率
     */
    @ApiModelProperty(value = "对人民币汇率", example = "")
    private double cnyRate;

    /**
     * 是否支持rpc接口，0否，1是
     */
    @ApiModelProperty(value = "是否支持rpc接口，0否，1是", example = "")
    private BooleanEnum enableRpc = BooleanEnum.IS_FALSE;

    /**
     * 是否是平台币
     */
    @ApiModelProperty(value = "是否是平台币", example = "")
    private BooleanEnum isPlatformCoin = BooleanEnum.IS_FALSE;

    /**
     * 最大提币手续费
     */
    @ApiModelProperty(value = "最大提币手续费", example = "")
    private double maxTxFee;

    /**
     * 最大提币数量
     */
    @ApiModelProperty(value = "最大提币数量", example = "")
    private BigDecimal maxWithdrawAmount;

    /**
     * 最小提币手续费
     */
    @ApiModelProperty(value = "最小提币手续费", example = "")
    private double minTxFee;

    /**
     * 最小提币数量
     */
    @ApiModelProperty(value = "最小提币数量", example = "")
    private BigDecimal minWithdrawAmount;

    /**
     * 中文名
     */
    @ApiModelProperty(value = "中文名", example = "")
    private String nameCn;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private int sort;

    /**
     * 状态 0正常，1禁用
     */
    @ApiModelProperty(value = "状态 0正常，1禁用", example = "")
    private CommonStatus status;

    /**
     * 币种缩写
     */
    @ApiModelProperty(value = "币种缩写", example = "")
    private String unit;

    /**
     * 对美元汇率
     */
    @ApiModelProperty(value = "对美元汇率", example = "")
    private double usdRate;

    /**
     * 提现阈值
     */
    @ApiModelProperty(value = "提现阈值", example = "")
    private BigDecimal withdrawThreshold;

    /**
     * 是否是合法币种  默认0
     */
    @ApiModelProperty(value = "是否是合法币种  默认0", example = "")
    private Boolean hasLegal = false;

    /**
     * 冷钱包地址
     */
    @ApiModelProperty(value = "冷钱包地址", example = "")
    private String coldWalletAddress;

    /**
     * 矿工费
     */
    @ApiModelProperty(value = "矿工费", example = "")
    private BigDecimal minerFee;

    /**
     * 主币
     */
    @ApiModelProperty(value = "主币", example = "")
    private String baseCoinUnit;

    /**
     * 提币精度
     */
    @ApiModelProperty(value = "提币精度", example = "")
    private int withdrawScale;

    /**
     * 对应区块链浏览器tx前缀
     */
    @ApiModelProperty(value = "对应区块链浏览器tx前缀", example = "")
    private String exploreUrl;

    /**
     * 手续费率
     */
    @ApiModelProperty(value = "手续费率", example = "")
    private BigDecimal feeRate;

    /**
     * 手续费模式0，定制，1比例
     */
    @ApiModelProperty(value = "手续费模式0，定制，1比例", example = "")
    private CoinFeeType feeType;

    /**
     * 是否具有标签 0 有，1无，默认1
     */
    @ApiModelProperty(value = "是否具有标签 0 有，1无，默认1", example = "")
    private CommonStatus hasLabel = CommonStatus.ILLEGAL;

    /**
     * 最小到账金额
     */
    @ApiModelProperty(value = "最小到账金额", example = "")
    private double minDepositAmount;

    @ApiModelProperty(value = "", example = "")
    private String content;

    /**
     * 抵扣数值，手续费抵扣模式为0时，代表具体数量，抵扣模式为1时，代表折扣0.8代表8折
     */
    @ApiModelProperty(value = "抵扣数值，手续费抵扣模式为0时，代表具体数量，抵扣模式为1时，代表折扣0.8代表8折", example = "")
    private BigDecimal feeDiscountAmount;

    /**
     * 手续费抵扣币种单位（不包括当前币种）
     */
    @ApiModelProperty(value = "手续费抵扣币种单位（不包括当前币种）", example = "")
    private String feeDiscountCoinUnit;

    /**
     * 手续费抵扣模式
     */
    @ApiModelProperty(value = "手续费抵扣模式", example = "")
    private CoinFeeType feeDiscountType;

    /**
     * 币种介绍链接
     */
    @ApiModelProperty(value = "币种介绍链接", example = "")
    private String moreLink;


}