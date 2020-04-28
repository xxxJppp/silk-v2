package com.spark.bitrade.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author davi
 */
@Data
@ApiModel(value = "矿池资金划转配置")
public class MinerTransferConfigDTO {
    @ApiModelProperty(value = "划转最低数量")
    private BigDecimal minimum;

    @ApiModelProperty(value = "派单佣金比例")
    private BigDecimal dispatchCommissionRate;

    @ApiModelProperty(value = "固定收益佣金比例")
    private BigDecimal fixedCommissionRate;

    @ApiModelProperty(value = "抢单佣金比例")
    private BigDecimal secKillCommissionRate;

    @ApiModelProperty(value = "App自动刷新矿池列表时间间隔，秒")
    private Long autoRefreshRate;
}
