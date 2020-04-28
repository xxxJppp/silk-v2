package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.07 09:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CoinMathForm implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 目标币种
     */
    @NotBlank
    @ApiModelProperty(value = "目标币种")
    private String targetCoin;

    /**
     * 支付币种
     */
    @NotBlank
    @ApiModelProperty(value = "支付币种")
    private String payCoin;

//    /**
//     * 申请人 id
//     */
//    @ApiModelProperty(value = "申请人id")
//    private Long memberId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

//    /**
//     *
//     */
//    @ApiModelProperty(value = "支付币种数量")
//    private BigDecimal payAmount;

//    /**
//     * 模块名称{0:引流交易码管理,1:交易对管理,2:转版管理}
//     */
//    @ApiModelProperty(value = "模块名称{0:引流交易码管理,1:交易对管理,2:转版管理}")
//    private ModuleType moduleType;

}
