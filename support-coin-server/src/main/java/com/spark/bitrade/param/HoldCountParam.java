package com.spark.bitrade.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.13 17:02
 */
@Data
public class HoldCountParam extends PageParam {

    /**
     * 持币数上限
     */
    @ApiModelProperty("持币数上限")
    private BigDecimal balanceStart;

    /**
     * 持币数下限
     */
    @ApiModelProperty("持币数下限")
    private BigDecimal balanceEnd;

}
