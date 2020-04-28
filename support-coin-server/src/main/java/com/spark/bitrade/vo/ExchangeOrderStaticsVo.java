package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.12 17:35
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "买卖盘统计Vo")
public class ExchangeOrderStaticsVo {

    @ApiModelProperty("买盘限价已成交总额")
    private BigDecimal buyLimitTradedTotal=BigDecimal.ZERO;
    @ApiModelProperty("买盘限价未成交总额")
    private BigDecimal buyLimitNoTradedTotal=BigDecimal.ZERO;
    @ApiModelProperty("买盘市价已成交总额")
    private BigDecimal buyMarketTradedTotal=BigDecimal.ZERO;
    @ApiModelProperty("买盘市价未成交总额")
    private BigDecimal buyMarketNoTradedTotal=BigDecimal.ZERO;


    @ApiModelProperty("卖盘限价已成交总额")
    private BigDecimal sellLimitTradedTotal=BigDecimal.ZERO;
    @ApiModelProperty("卖盘限价未成交总额")
    private BigDecimal sellLimitNoTradedTotal=BigDecimal.ZERO;
    @ApiModelProperty("卖盘市价已成交总额")
    private BigDecimal sellMarketTradedTotal=BigDecimal.ZERO;
    @ApiModelProperty("卖盘市价未成交总额")
    private BigDecimal sellMarketNoTradedTotal=BigDecimal.ZERO;





    @ApiModelProperty("有效用户数")
    private Integer validPersons=0;

}
