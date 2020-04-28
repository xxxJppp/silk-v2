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

/**
 * 经纪人优惠兑币限额记录(AmountOfDiscountRecord)实体类
 *
 * @author makejava
 * @since 2020-04-08 15:58:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel
public class AmountOfDiscountRecord implements Serializable {
    private static final long serialVersionUID = 771454574585844572L;
    /**
    * id
    */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
    * member_id
    */
    private Long memberId;
    /**
    * 优惠总额度
    */
    @ApiModelProperty("优惠总额度")
    private BigDecimal totalAmountOfDiscount;
    /**
    * 优惠剩余额度
    */
    @ApiModelProperty("优惠剩余额度")
    private BigDecimal remainingAmountOfDiscount;
    /**
    * 优惠使用额度
    */
    @ApiModelProperty("优惠使用额度")
    private BigDecimal usedAmountOfDiscount;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;


}