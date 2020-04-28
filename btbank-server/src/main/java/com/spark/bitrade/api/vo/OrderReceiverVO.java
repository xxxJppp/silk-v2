package com.spark.bitrade.api.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author davi
 */
@Data
@ApiModel(value = "订单详情")
public class OrderReceiverVO {
    @ApiModelProperty(value = "订单编号")
    @JSONField(name = "order_sn")
    @JsonProperty(value = "order_sn")
    private String orderSn;

    @ApiModelProperty(value = "订单金额")
    @JSONField(name = "total_price")
    @JsonProperty(value = "total_price")
    private String totalPrice;

    @ApiModelProperty(value = "参数校验码")
    private String sign;
}
