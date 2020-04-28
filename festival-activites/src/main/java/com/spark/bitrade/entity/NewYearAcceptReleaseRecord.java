package com.spark.bitrade.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
public class NewYearAcceptReleaseRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 令牌id
     */
    private String token;

    /**
     * 数量
     */
    private BigDecimal acceptAmount;

    /**
     * 币种
     */
    private String coinUnit;

    /**
     * 类型{0:领奖1:释放}
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;


}
