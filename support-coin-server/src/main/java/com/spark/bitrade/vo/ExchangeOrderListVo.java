package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.12 17:41
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "买卖盘统计明细Vo")
public class ExchangeOrderListVo {

    @ApiModelProperty(value = "申请人ID")
    private Long memberId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "交易对")
    private String coinMatch;

    @ApiModelProperty(value = "挂单类型")
    private String deityType;

    @ApiModelProperty(value = "挂单时间")
    private Long deityTime;

    @ApiModelProperty(value = "挂单价格")
    private BigDecimal deityPrice;

    @ApiModelProperty(value = "成交时间")
    private Long dealTime;

    @ApiModelProperty(value = "成交价格")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "成交数量")
    private BigDecimal dealNum;

    @ApiModelProperty(value = "未成交数量")
    private BigDecimal noDealNum;


}
