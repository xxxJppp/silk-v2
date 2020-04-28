package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * (Advertise)实体类
 *
 * @author ss
 * @date 2020-03-19 10:22:02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("广告")
public class Advertise implements Serializable {
    private static final long serialVersionUID = 560721614475005151L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "广告ID")
    private Long id;
    /**
    * 广告类型:0:买入 1:卖出
    */
    @NotNull(message = "{advertiseType不能为空}")
    @ApiModelProperty(value = "广告类型:0:买入 1:卖出")
    private Integer advertiseType;
    /**
    * 是否开启自动回复
    */
    @ApiModelProperty(value = "是否开启自动回复")
    private Integer auto;
    /**
     * 自动回复内容
     */
    @ApiModelProperty(value = "自动回复内容")
    private String autoword;
    /**
     * 币种缩写
     */
    @ApiModelProperty(value = "币种缩写")
    private String coinUnit;
    /**
    * 广告创建时间
    */
    @ApiModelProperty(value = "广告创建时间")
    private Date createTime;
    /**
    * 交易中数量
    */
    @ApiModelProperty(hidden = true,value = "交易中数量")
    private BigDecimal dealAmount;
    /**
    * 广告级别:0=普通/1=优质
    */
    @ApiModelProperty(value = "广告级别:0=普通/1=优质")
    private Integer level;

    private String limitMoney;
    /**
    * 最高单笔交易额
    */
    @ApiModelProperty(value = "最高单笔交易额")
    private BigDecimal maxLimit;
    /**
    * 最低单笔交易额
    */
    @ApiModelProperty(value = "最低单笔交易额")
    private BigDecimal minLimit;
    /**
    * 计划数量
    */
    @ApiModelProperty(value = "计划数量")
    private BigDecimal number;
    /**
    * 付费方式:用英文逗号隔开
    */
    @ApiModelProperty(hidden = true,value = "付费方式:用英文逗号隔开")
    private String payMode;
    /**
    * 溢价百分比
    */
    @ApiModelProperty(value = "溢价百分比")
    private BigDecimal premiseRate;
    /**
    * 交易价格
    */
    @ApiModelProperty(value = "交易价格")
    private BigDecimal price;
    /**
    * 价格类型:0=固定的/1=变化的
    */
    @ApiModelProperty(value = "价格类型:0=固定的/1=变化的")
    private Integer priceType;
    /**
    * 计划剩余数量
    */
    @ApiModelProperty(hidden = true,value = "计划剩余数量")
    private BigDecimal remainAmount;
    /**
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
    * 广告上下架状态:0=上架/1=下架/2=已关闭（删除）
    */
    @ApiModelProperty(hidden = true,value = "广告上下架状态:0=上架/1=下架/2=已关闭（删除）/3失效")
    private Integer status = 0;
    /**
    * 付款期限
    */
    @ApiModelProperty(value = "付款期限")
    private Integer timeLimit;
    /**
    * 广告最后更新时间
    */
    @ApiModelProperty(hidden = true,value = "广告最后更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "username")
    private String username;

    @ApiModelProperty(hidden = true,value = "version")
    private Long version;
    /**
    * 币种:引用Otc_Coin表
    */
    @ApiModelProperty(value = "Otc_Coin表ID")
    private Long coinId;
    /**
    * 国家:应用county表
    */
    @ApiModelProperty(hidden = true,value = "country")
    private String country;
    /**
    * 广告拥有者
    */
    @ApiModelProperty(hidden = true)
    private Long memberId;
    /**
    * 需要交易方已绑定手机
    */
    @ApiModelProperty(value = "需要交易方已绑定手机?0否1是")
    private Integer needBindPhone;
    /**
    * 是否使用优惠支付,0使用，1不使用
    */
    @ApiModelProperty(value = "是否使用优惠支付,0使用，1不使用")
    private Integer needPutonDiscount;
    /**
    * 需要交易方已进行实名认证
    */
    @ApiModelProperty("需要交易方已进行实名认证 0放1是")
    private Integer needRealname;
    /**
    * 需要交易方至少完成过N笔交易（默认为0）
    */
    @ApiModelProperty("需要交易方至少完成过N笔交易（默认为0）")
    private Integer needTradeTimes;
    /**
    * 同时最大处理订单数 (0 = 不限制)
    */
    @ApiModelProperty("同时最大处理订单数 (0 = 不限制)")
    private Integer maxTradingOrders;
    /**
    * 广告置顶，值越大越前面
    */
    private Integer sort;
    /**
     * 法币ID
     */
    @ApiModelProperty(value = "法币ID")
    private Long currencyId;


}
