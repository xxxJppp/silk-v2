package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 幸运宝-对应币种表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LuckyManageCoin implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 活动id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    private Long numId;

    /**
     * 币种
     */
    private String coinUnit;

    /**
     * 描述
     */
    private String description;

    /**
     * 赛牛开始价
     */
    private BigDecimal startPrice;

    /**
     * 赛牛结束价
     */
    private BigDecimal endPrice;

    /**
     * 涨幅
     */
    private BigDecimal increase;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建用户id
     */
    private Long createUserid;

    /**
     * 修改用户
     */
    private Long updateUserid;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private BooleanEnum deleteFlag;


}
