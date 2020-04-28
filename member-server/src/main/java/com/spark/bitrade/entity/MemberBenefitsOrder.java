package com.spark.bitrade.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 会员申请订单
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberBenefitsOrder implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long memberExtendId;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    /**
     * 操作类型
     * 10-续费
     * 20-升级
     * 30-开通
     */
    @ApiModelProperty(value = "操作类型 {10-续费, 20-升级, 30-开通}")
    private Integer operateType;

    /**
     * 原会员等级
     */
    @ApiModelProperty(value = "原会员等级")
    private Integer originLevel;

    /**
     * 新会员等级
     */
    @ApiModelProperty(value = "新会员等级")
    private Integer destLevel;

    /**
     * 生效时间
     */
    @ApiModelProperty(value = "生效时间")
    private Date startTime;

    /**
     * 失效时间
     */
    @ApiModelProperty(value = "失效时间")
    private Date endTime;

    /**
     * 支付类型
     * 10-购买
     * 20-锁仓
     */
    @ApiModelProperty(value = "支付类型 {10-购买, 20-锁仓}")
    private Integer payType;

    /**
     * 费用
     */
    @ApiModelProperty(value = "费用")
    private BigDecimal amount;

    /**
     * 费用的币种单位
     */
    @ApiModelProperty(value = "费用的币种单位")
    private String unit;

    /**
     * 客户端来源
     */
    @ApiModelProperty(value = "客户端来源")
    private Integer appId;

    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间")
    private Date payTime;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "会员等级")
    private String levelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "有效期")
    private Integer validityDays;

    private String orderMqId;

    private Long lockDetailId;

    public static String MEMBER_EXTEND_ID = "member_extend_id";

    public static String CREATE_TIME = "create_time";
}
