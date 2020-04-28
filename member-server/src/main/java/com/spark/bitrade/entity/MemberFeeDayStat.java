package com.spark.bitrade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员购买情况日统计
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MemberFeeDayStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 时间yyyy-MM-dd
     */
    private Date statisticDate;

    /**
     * 币种
     */
    private String unit;

    /**
     * 购买方式的费用总数
     */
    private BigDecimal buyUnitQuantity = new BigDecimal(0);

    /**
     * 锁仓方式的费用总数
     */
    private BigDecimal lockUnitQuantity = new BigDecimal(0);

    /**
     * 购买人次
     */
    private Long buyCount = 0l;

    /**
     * 锁仓人次
     */
    private Long lockCount = 0l;

    /**
     * 购买费用的总返佣数量
     */
    private BigDecimal buyCommision = new BigDecimal(0);

    /**
     * 锁仓方式费用的总返佣数量
     */
    private BigDecimal lockCommision = new BigDecimal(0);

    /**
     * 锁仓到期释放的总币数
     */
    private BigDecimal unlockUnitQuantity = new BigDecimal(0);

    /**
     * 记录版本，高并发使用
     */
    private Long version = 1l;

    private Date createTime;

    private Date lastUpdateTime;


}
