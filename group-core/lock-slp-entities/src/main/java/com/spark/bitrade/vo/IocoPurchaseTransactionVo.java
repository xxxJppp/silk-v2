package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ioco申购记录
 * @author shenzucai
 * @time 2019.07.03 16:16
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "ioco申购记录对象")
public class IocoPurchaseTransactionVo {

    /**
     * 记录类型（0申购，1是赠送）
     */
    @ApiModelProperty(value = "记录类型（0申购，1是赠送）", example = "")
    private Integer type;
    /**
     * 发起币种 type为0 是可用，对应页面'使用'
     */
    @ApiModelProperty(value = "发起币种 type为0 是可用，对应页面'使用'", example = "")
    private String fromUnit;

    /**
     * 发起金额 type为0 是可用，对应页面'使用'
     */
    @ApiModelProperty(value = "发起金额 type为0 是可用，对应页面'使用'", example = "")
    private BigDecimal fromAmount;

    /**
     * 接收币种 当type=1是，该字段对应页面'转出赠送'，type=0对应页面'申购'
     */
    @ApiModelProperty(value = "接收币种 当type=1是，该字段对应页面'转出赠送'，type=0对应页面'申购'", example = "")
    private String toUnit;

    /**
     * 接收金额
     */
    @ApiModelProperty(value = "接收金额 当type=1是，该字段对应页面'转出赠送'，type=0对应页面'申购'", example = "")
    private BigDecimal toAmount;

    /**
     * 赠送对象账号（手机号或邮箱）type=1时，页面'交易对象'
     */
    @ApiModelProperty(value = "赠送对象账号（手机号或邮箱）type=1时，页面'交易对象'", example = "")
    private String gitfMemberAccount;

    /**
     * 0转入 1转出
     */
    @ApiModelProperty(value = "0转入 1转出", example = "")
    private Integer transferType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;
}
