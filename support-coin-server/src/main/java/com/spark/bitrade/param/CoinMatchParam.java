package com.spark.bitrade.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.12 13:53
 */
@Data
public class CoinMatchParam extends PageParam{

    @ApiModelProperty("目标币种")
    private String targetCoin;
}
