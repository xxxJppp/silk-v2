package com.spark.bitrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.12 14:33  
 */
@Data
public class WidthRechargeStaticsVo {

    private Long memberId;

    private String userName;

    private Date createTime;

    private BigDecimal amount;

}
