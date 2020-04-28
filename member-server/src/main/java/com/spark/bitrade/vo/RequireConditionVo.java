package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 17:04
 */
@Data
@ApiModel(description = "会员等级购买、锁仓费用，经纪人社区要求人数规则Vo")
public class RequireConditionVo {

    @ApiModelProperty(value = "原购买费用")
    private BigDecimal buyAmount;

    @ApiModelProperty(value = "原锁仓费用")
    private BigDecimal lockAmount;

    @ApiModelProperty(value = "折扣后购买费用")
    private BigDecimal DiscountbuyAmount;

    @ApiModelProperty(value = "折扣后锁仓费用")
    private BigDecimal DiscountLockAmount;

    @ApiModelProperty(value = "vip等级名称")
    private String vipLevelName;

    @ApiModelProperty(value = "经纪人社区人数")
    private BigDecimal CommunityNumber;

    @ApiModelProperty(value = "是否有折扣 {1有，0没有}")
    private Integer buyDiscountFlag;

    @ApiModelProperty(value = "是否有折扣 {1有，0没有}")
    private Integer lockDiscountFlag;

    private Integer levelId;

    @ApiModelProperty(value = "Vip级别名字")
    private String levelName;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String unit;

    /**
     * 开通时长，只能是30的倍数
     */
    @ApiModelProperty(value = "开通时长")
    private Integer duration;

}
