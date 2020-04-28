package com.spark.bitrade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  
 *
 * @author young
 * @time 2019.07.25 18:53
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "当前参与的套餐信息")
public class SlpMylockinfo {
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
    private BigDecimal amount;


    /**
     * @param entity
     * @return
     */
    public static SlpMylockinfo convert(LockCoinDetail lockCoinDetail, LockCoinActivitieSetting entity) {
        return SlpMylockinfo.builder()
                .name(entity.getName())
                .coinSymbol(entity.getCoinSymbol())
                .amount(lockCoinDetail.getTotalAmount())
                .build();
    }
}
