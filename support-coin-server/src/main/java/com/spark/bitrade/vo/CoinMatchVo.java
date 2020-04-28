package com.spark.bitrade.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.SupportCoinMatch;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 17:27
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "交易对Vo")
public class CoinMatchVo {
    /**
     * 币种基本信息修改申请界面 交易对显示
     */
    @ApiModelProperty(value = "币种基本信息修改申请界面 交易对显示  如：BTT/TTBT")
    private String coinMatchName;

    /**
     * 是否可以新增交易对标识
     * 0 -> 可以
     * 1 -> 不可以
     */
    @ApiModelProperty(value = "是否可以新增交易对标识， 0 -> 可以; 1 -> 不可以 ")
    private String isAddCoinMacth;

    /**
     * 交易对明细列表
     */
    @ApiModelProperty(value = "交易对明细列表")
    private IPage<SupportCoinMatch> coinMatches;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "项目方币种")
    private String coinName;


}
