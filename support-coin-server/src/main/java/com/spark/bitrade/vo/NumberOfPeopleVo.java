package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  
 *   人数金额统计
 *  @author liaoqinghui  
 *  @time 2019.11.12 16:07  
 */
@Data
public class NumberOfPeopleVo {

    @ApiModelProperty(value = "充值总币数")
    private BigDecimal rechargeTotal=BigDecimal.ZERO;
    @ApiModelProperty(value = "已提现币数")
    private BigDecimal withTotal=BigDecimal.ZERO;
    @ApiModelProperty(value = "提现审核中币数")
    private BigDecimal withToAuditTotal=BigDecimal.ZERO;
    @ApiModelProperty(value = "提现审核中人数")
    private Integer withToAuditPersons=0;
    @ApiModelProperty(value = "已提现人数")
    private Integer withedPerson=0;
    @ApiModelProperty(value = "有效用户shu数")
    private Integer validPersonCount=0;

}
