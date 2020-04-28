package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币交易-推荐人累计买入奖励累计表(ExchangeReleaseAwardTotal)表实体类
 *
 * @author yangch
 * @since 2020-02-05 17:31:07
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易-推荐人累计买入奖励累计表")
public class ExchangeReleaseAwardTotal {

    /**
     * 会员ID
     */
    @TableId
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String symbol;

    /**
     * 直推会员累计买入数量
     */
    @ApiModelProperty(value = "直推会员累计买入数量", example = "")
    private BigDecimal totalBuyAmount;

    /**
     * 累计已发放奖励次数
     */
    @ApiModelProperty(value = "累计已发放奖励次数", example = "")
    private Integer totalAwardTimes;

    /**
     * 累计已发放奖励
     */
    @ApiModelProperty(value = "累计已发放奖励", example = "")
    private BigDecimal totalAwardAmount;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新日期
     */
    @ApiModelProperty(value = "更新日期", example = "")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, update = "NOW()")
    private Date updateTime;


}