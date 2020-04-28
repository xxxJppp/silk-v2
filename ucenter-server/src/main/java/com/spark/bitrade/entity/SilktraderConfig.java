package com.spark.bitrade.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 交易所初始化信息实体
 * @author: ss
 * @date: 2020/2/27
 */
@Data
public class SilktraderConfig {
    @ApiModelProperty(value = "交易对配置信息")
    private List<SymbolConifg> symbolConfig;
}
