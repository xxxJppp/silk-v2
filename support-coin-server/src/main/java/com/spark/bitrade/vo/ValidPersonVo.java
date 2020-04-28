package com.spark.bitrade.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.18 11:31  
 */
@Data
public class ValidPersonVo {

    private Long memberId;

    private BigDecimal totalAmount;
}
