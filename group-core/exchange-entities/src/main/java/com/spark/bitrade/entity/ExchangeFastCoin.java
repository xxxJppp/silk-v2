package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 闪兑币种配置(ExchangeFastCoin)表实体类
 *
 * @author yangch
 * @since 2019-06-24 17:06:44
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "闪兑币种配置")
public class ExchangeFastCoin {

    /**
     * id,可用是baseSymbol，coinSymbol，appId组合
     */
    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = "主键ID", example = "", required = true)
    private String id;

    /**
     * 闪兑基币币种名称,如CNYT、BT
     */
    @ApiModelProperty(value = "闪兑基币币种名称,如CNYT、BT", example = "CNYT", required = true)
    private String baseSymbol;

    /**
     * 闪兑币种名称，如BTC、LTC
     */
    @ApiModelProperty(value = "闪兑币种名称，如BTC、LTC", example = "BTC", required = true)
    private String coinSymbol;

    /**
     * 兑换基币实时汇率参考币种名称，非必填项，可用为null
     */
    @ApiModelProperty(value = "兑换基币实时汇率参考币种名称", example = "CNYT", required = false)
    private String rateReferenceBaseSymbol;

    /**
     * 兑换基币的固定费率，非必填项。如该值大于1，则“rateReferenceBaseSymbol”无效
     */
    @ApiModelProperty(value = "兑换基币的固定费率", example = "1")
    private BigDecimal baseSymbolFixedRate;

    /**
     * 兑换币种实时汇率参考币种名称，可用为null
     */
    @ApiModelProperty(value = "兑换币种实时汇率参考币种名称", example = "BTC", required = false)
    private String rateReferenceCoinSymbol;

    /**
     * 兑换币种的固定费率
     */
    @ApiModelProperty(value = "兑换币种的固定费率", example = "")
    private BigDecimal coinSymbolFixedRate;

    /**
     * 买入时价格上调默认比例,取值[0-1]；闪兑用户买入时，基于实时价的上调价格的浮动比例。
     */
    @ApiModelProperty(value = "买入时价格上调默认比例,取值[0-1]", example = "0.05", required = true)
    private BigDecimal buyAdjustRate;

    /**
     * 卖出时价格下调默认比例，取值[0-1]；闪兑用户买出时，基于实时价的下调价格的浮动比例。
     */
    @ApiModelProperty(value = "卖出时价格下调默认比例，取值[0-1]", example = "0.05", required = true)
    private BigDecimal sellAdjustRate;

    /**
     * 启用状态，1=启用/0=禁止
     */
    @ApiModelProperty(value = "启用状态，1=启用/0=禁止", example = "1")
    private BooleanEnum enable;

    /**
     * 渠道/应用ID
     */
    @ApiModelProperty(value = "应用ID", example = "0", required = true)
    private String appId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2019-04-03 00:00:00")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "2019-04-03 00:00:00")
    private Date updateTime;

}