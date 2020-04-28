package com.spark.bitrade.api.vo;

import com.spark.bitrade.constant.WalletType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * AssetsVo
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 17:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "资产实体")
public class AssetsVo {


    @ApiModelProperty(value = "类型", example = "")
    private WalletType type;

    @ApiModelProperty(value = "名称", example = "")
    private String name;

    @ApiModelProperty(value = "资产数额, USDT", example = "")
    private BigDecimal amount;

    @ApiModelProperty(value = "资产数额, CNY", example = "")
    private BigDecimal cny;

    /**
     * 添加数额
     *
     * @param amount amount
     * @return this;
     */
    public AssetsVo add(BigDecimal amount) {
        if (amount == null) {
            return this;
        }

        if (this.amount == null) {
            this.amount = amount;
            return this;
        }

        this.amount = this.amount.add(amount);
        return this;
    }

    /**
     * 添加CNY
     *
     * @param cny cny
     * @return this
     */
    public AssetsVo cny(BigDecimal cny) {
        if (cny == null) {
            return this;
        }

        if (this.cny == null) {
            this.cny = cny;
            return this;
        }

        this.cny = this.cny.add(cny);
        return this;
    }
}
