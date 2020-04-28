package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 领奖记录
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearStatics implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 统计时间
     */
    private String collectDate;

    /**
     * 奖励币种
     */
    private String coinUnit;

    /**
     * 已发数量 用户得到该币种的总量 锁仓+可用
     */
    private BigDecimal sendAmount;

    /**
     * 锁仓数量 锁仓
     */
    private BigDecimal lockAmount;

    /**
     * 已释放数量 = 已释放+可用
     */
    private BigDecimal releasedAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
