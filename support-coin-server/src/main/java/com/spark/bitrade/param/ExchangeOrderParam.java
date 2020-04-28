package com.spark.bitrade.param;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.13 09:38
 */
@Data
public class ExchangeOrderParam extends PageParam {

    /**
     * 0买盘 1卖盘
     */
    @ApiModelProperty("0买盘 1卖盘")
    private ExchangeOrderDirection direction;
    /**
     *0交易中 1已成交
     */
    @ApiModelProperty("0交易中（未成交） 1已成交")
    private ExchangeOrderStatus status;


}
