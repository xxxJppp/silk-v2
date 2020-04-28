package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  布朗计划参与记录vo
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.09 14:15  
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("serial")
@Data
@ApiModel(description = "布朗计划参与记录vo")
@Builder
public class LockSlpMemberRecordVo {

    @ApiModelProperty(value = "锁仓奖励基金剩余币数=待释放币数", example = "1000.000")
    private BigDecimal toBeReleasedAmount;

    @ApiModelProperty(value = "已释放SLP", example = "200.00")
    private BigDecimal hasBeenReleasedAmount;

}














