package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 大乐透总奖池表(LockSlpJackpot)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:26:58
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "大乐透总奖池表")
public class LockSlpJackpot {

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id", example = "")
    private Long id;

    /**
     * 币种，必须大写
     */
    @ApiModelProperty(value = "币种，必须大写", example = "")
    private String coinUnit;

    /**
     * 当前期数
     */
    @ApiModelProperty(value = "当前期数", example = "")
    private Integer currentPeriod;

    /**
     * 奖池币数
     */
    @ApiModelProperty(value = "奖池币数", example = "")
    private BigDecimal jackpotAmount;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;


}