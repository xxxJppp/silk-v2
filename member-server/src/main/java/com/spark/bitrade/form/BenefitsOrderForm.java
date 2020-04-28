package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.19 18:11
 */
@Data
public class BenefitsOrderForm implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 支付类型
     * 10-购买
     * 20-锁仓
     */
    @NotNull
    @ApiModelProperty(value = "{ 10-购买, 20-锁仓 }")
    private Integer payType;

    /**
     * 操作类型
     * 10-续费
     * 20-升级
     * 30-开通
     */
    @NotNull
    @ApiModelProperty(value = "{ 10-续费, 20-升级, 30-开通 }")
    private Integer operateType;

    /**
     * 币种
     */
    @NotBlank
    @ApiModelProperty(value = "币种")
    private String unit;

    /**
     * 费用的币种单位
     */
//    @ApiModelProperty(value = "费用的币种单位")
//    private BigDecimal quantity;

    /**
     * 开通时长，只能是30的倍数
     */
    @NotNull
    @ApiModelProperty(value = "开通时长")
    private Integer duration;

    /**
     * 开通的会员等级
     * { 1-普通会员, 2-vip1, 3-vip2, 4-vip3, 5-经纪人 }
     */
    @ApiModelProperty(value = "开通的会员等级 levelId { 1-普通会员, 2-vip1, 3-vip2, 4-vip3, 5-经纪人 }")
    private Integer vipLevel;

    /**
     * 当前的会员等级
     * { 1-普通会员, 2-vip1, 3-vip2, 4-vip3, 5-经纪人 }
     */
    @ApiModelProperty(value = "当前的会员等级 levelId { 1-普通会员, 2-vip1, 3-vip2, 4-vip3, 5-经纪人 }")
    private Integer currentMemberLevel;

    /**
     * 客户端来源
     */
//    @NotBlank
    @ApiModelProperty(value = "客户端来源")
    private Integer appId;

    /**
     * 费用
     */
    @NotNull
    @ApiModelProperty(value = "费用")
    private BigDecimal amount;

    @ApiModelProperty(value = "社区人数")
    private Integer communitySize;

}
