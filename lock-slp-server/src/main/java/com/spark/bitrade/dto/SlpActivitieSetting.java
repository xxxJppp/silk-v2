package com.spark.bitrade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  SLP活动信息
 *
 * @author young
 * @time 2019.07.10 11:00
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "锁仓活动方案配置")
public class SlpActivitieSetting {
    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID", example = "")
    private Long id;

    /**
     * 套餐名称
     */
    @ApiModelProperty(value = "套餐名称", example = "")
    private String name;

    /**
     * 活动币种
     */
    @ApiModelProperty(value = "活动币种", example = "")
    private String coinSymbol;

    /**
     * 活动投入数量
     */
    @ApiModelProperty(value = "活动投入数量", example = "")
    private BigDecimal minBuyAmount;

    /**
     * 支付币种
     */
    @ApiModelProperty(value = "支付币种", example = "")
    private String paycoinSymbol;

    /**
     * 支付数量
     */
    @ApiModelProperty(value = "支付数量", example = "")
    private BigDecimal payAmout;

    /**
     * 每日释放数量(SLP)
     */
    @ApiModelProperty(value = "每日释放数量", example = "")
    private BigDecimal releaseAmountPerDay;


    /**
     * 总收益(USDT)
     */
    @ApiModelProperty(value = "总收益(USDT)", example = "")
    private BigDecimal planEarningsAmount;

    /**
     * 参与类型
     */
    @ApiModelProperty(value = "参与类型（0/NONE=不能参与，1/JOIN=可以参与，2/UPGRADE=可以升仓）", example = "")
    private Type type;


    /**
     * @param entity
     * @param earningRate   收益比例
     * @param payCoinSymbol 支付币种
     * @param payAmout      支付数量
     * @param type          参与类型
     * @return
     */
    public static SlpActivitieSetting convert(LockCoinActivitieSetting entity,
                                              BigDecimal earningRate, String payCoinSymbol, BigDecimal payAmout, Type type) {
        return SlpActivitieSetting.builder()
                .id(entity.getId())
                .name(entity.getName())
                .coinSymbol(entity.getCoinSymbol())
                .minBuyAmount(entity.getMinBuyAmount())
                .releaseAmountPerDay(entity.getMinBuyAmount()
                        .multiply(new BigDecimal(entity.getCycleRatio())).setScale(4, BigDecimal.ROUND_DOWN))
                .planEarningsAmount(entity.getMinBuyAmount().multiply(earningRate).setScale(4, BigDecimal.ROUND_DOWN))
                .paycoinSymbol(payCoinSymbol)
                .payAmout(payAmout)
                .type(type)
                .build();
    }


    //参与类型
    public enum Type {
        NONE, JOIN, UPGRADE;
    }
}
