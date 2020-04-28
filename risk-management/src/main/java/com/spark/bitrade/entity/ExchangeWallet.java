package com.spark.bitrade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户币币账户
 * </p>
 *
 * @author qiliao
 * @since 2020-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExchangeWallet implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID, MemberId:CoinUnit
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 币种
     */
    private String coinUnit;

    /**
     * 是否锁定
     */
    private Integer isLock;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 签名
     */
    private String signature;




}
