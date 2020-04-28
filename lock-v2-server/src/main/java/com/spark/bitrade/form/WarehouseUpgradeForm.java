package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 升仓form 
 *
 * @author zhongxj
 * @time 2019.09.02
 */
@Data
public class WarehouseUpgradeForm {
    /**
     * 资金密码
     */
    @NotBlank
    @ApiModelProperty(value = "资金密码")
    private String jyPassword;
    /**
     * 升仓数量
     */
    @ApiModelProperty(value = "升仓数量")
    private BigDecimal num;
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
}
