package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 高风险用户
 * </p>
 *
 * @author qiliao
 * @since 2020-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskHighMember implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户名称
     */
    private String memberName;

    /**
     * 出金总计
     */
    private BigDecimal outSum;

    /**
     * 入金总计
     */
    private BigDecimal inSum;

    /**
     * 风险系数
     */
    private BigDecimal coefficient;

    /**
     * 出场时间
     */
    private Date outTime;

    /**
     * 核查状态0未核查1合法-1不合法
     */
    private String examineStatus;

    /**
     * 核查人id
     */
    private Long examineMemberId;

    /**
     * 核查时间
     */
    private Date examineTime;

    /**
     * 核查备注
     */
    private String examineDesc;

}
