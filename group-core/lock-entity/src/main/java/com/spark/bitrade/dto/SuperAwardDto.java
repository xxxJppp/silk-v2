package com.spark.bitrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.18 11:40  
 */
@Data
public class SuperAwardDto {

    /**
     * 币币交易的用户id
     */
    private Long memberId;
    /**
     * 手续费奖励 数据库已经做了计算
     */
    private BigDecimal awardFee;
    /**
     * 币种
     */
    private String coinUnit;

    /**
     * 合伙人id
     */
    private Long partnerId;

    /**
     * 交易金额
     */
    private BigDecimal totalAmount;

    private String baseCoin;

    /**
     * 补救时的创建时间
     */
    private Date createDate;
}
