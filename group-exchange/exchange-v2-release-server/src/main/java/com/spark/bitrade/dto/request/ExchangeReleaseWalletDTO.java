package com.spark.bitrade.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 *   DTO
 * @author lc
 * @since 2019/12/17
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeReleaseWalletDTO {

    private static final long serialVersionUID = -7151685362718045801L;

    /**
     * @parm 用户ID:币种名称
     */
    @NotNull(message = "id不能为空")
    private String id;

    /**
     * memberId
     */
    @NotNull(message = "memberId不能为空")
    @ApiModelProperty(value = "memberId")
    private Integer memberId;

    /**
     * @parm 币种名称
     */
    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String coinSymbol;


    /**
     * @parm  锁仓数量
     */
    @NotBlank(message = "锁仓数量不能为空")
    @ApiModelProperty(value = "锁仓数量")
    private BigDecimal lockAmount;


    @Override
    public String toString() {
        return "ExchangeReleaseWalletDTO{" +
                "id='" + id + '\'' +
                ", memberId=" + memberId +
                ", coinSymbol='" + coinSymbol + '\'' +
                ", lockAmount='" + lockAmount + '\'' +
                '}';
    }
}
