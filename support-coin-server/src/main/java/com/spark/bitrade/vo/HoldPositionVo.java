package com.spark.bitrade.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.22 09:30  
 */
@Data
public class HoldPositionVo {

    private IPage<MembertVo> page;

    /**
     * 持仓用户数
     */
    @ApiModelProperty(value = "持仓用户数", example = "")
    private Integer holdUserNum=0;

    /**
     * 持仓币数
     */
    @ApiModelProperty(value = "持仓币数", example = "")
    private BigDecimal holdCoinNums=BigDecimal.ZERO;

    /**
     * 有效用户数
     */
    @ApiModelProperty(value = "有效用户数", example = "")
    private Integer effectiveUserNum=0;

    /**
     * 有效用户持仓币数
     */
    @ApiModelProperty(value = "有效用户持仓币数", example = "")
    private BigDecimal holdEffectiveUserNum=BigDecimal.ZERO;

}
