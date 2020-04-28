package com.spark.bitrade.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员申请条件
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberRequireCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer levelId;

    /**
     * 类型
     * 10-购买
     * 20-锁仓
     * 30-建立社区
     */
    private Integer type;

    /**
     * 币种
     */
    private String unit;

    /**
     * 费用数量
     */
    private BigDecimal quantity;

    /**
     * 开通时长，只能是30的倍数
     */
    private Integer duration;

    /**
     * 此类型是否折扣开关
     * 1-开
     * 0-关
     */
    private Integer flagDiscount;

    /**
     * 折扣率
     */
    private BigDecimal discount;

    /**
     * 关联条件ID
     */
    private Integer conditionId;

    /**
     * 与关联条件的关系
     * 10-AND
     * 20-OR
     */
    private Integer conditionRelationship;

    private Date createTime;

    private Date updateTime;


    public static String LEVEL_ID = "level_id";

}
