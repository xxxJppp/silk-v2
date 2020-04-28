package com.spark.bitrade.entity.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.Country;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @description:
 * @author: ss
 * @date: 2020/3/22
 */
@Data
@ApiModel(value="广告详情", description="广告详情")
public class MemberAdvertiseDetail implements Serializable {

    @ApiModelProperty(value = "广告id")
    private Long id;

    @ApiModelProperty("币种id")
    private Long coinId;

    private String coinName;

    private String coinNameCn;

    private String coinUnit;


    /**
     * 法币名称
     */
    private String currencyName;

    /**
     * 法币单位
     */
    private String currencyUnit;

    /**
     * 法币符号
     */
    private String currencySymbol;
    private Integer priceType;


    /**
     * 交易价格(及时变动)
     */
    private BigDecimal price;

    /**
     * 广告类型 0:买入 1:卖出
     */
    private Integer advertiseType;


    /**
     * 最低单笔交易额
     */
    private BigDecimal minLimit;

    /**
     * 最高单笔交易额
     */
    private BigDecimal maxLimit;

    /**
     * 备注
     */
    private String remark;

    /**
     * 付款期限，单位分钟
     */
    private Integer timeLimit;

    /**
     * 溢价百分比
     */
    private BigDecimal premiseRate;


    /**
     * 付费方式(用英文逗号隔开)
     */
    private String payMode;


    /**
     * 广告状态
     */
    private Integer status;

    private BigDecimal number;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    private BooleanEnum auto;

    private String autoword;

    //add by yangch 时间： 2018.10.24 原因： 1.3优化需求的扩展字段 --begin
    /**
     * 需要交易方已绑定手机号
     */
    private int needBindPhone = BooleanEnum.IS_FALSE.getOrdinal();

    /**
     * 需要交易方已做实名认证
     */
    private int needRealname = BooleanEnum.IS_FALSE.getOrdinal();

    /**
     * 需要交易方至少完成过N笔交易（默认为0）
     */
    //edit by tansitao 时间： 2018/10/25 原因：添加默认值
    private int needTradeTimes = 0;

    /**
     * 是否使用优惠币种支付（默认为0）
     */
    private int needPutonDiscount = BooleanEnum.IS_FALSE.getOrdinal();
    //add by yangch 时间： 2018.10.24 原因： 1.3优化需求的扩展字段 --end

    //add by tansitao 时间： 2018/11/19 原因：同时最大处理订单数 (0 = 不限制)
    private int maxTradingOrders = 0;


}
