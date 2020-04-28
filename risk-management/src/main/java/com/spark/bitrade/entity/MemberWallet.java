package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2020-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberWallet implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String address;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    private Long memberId;

    private Integer version;

    private String coinId;

    /**
     * 钱包是否锁定
     */
    private Integer isLock;

    /**
     * 锁仓余额
     */
    private BigDecimal lockBalance;

    /**
     * 启动充值，0=禁用/1=启用
     */
    private Integer enabledIn;

    /**
     * 启动提币，0=禁用/1=启用
     */
    private Integer enabledOut;

}
