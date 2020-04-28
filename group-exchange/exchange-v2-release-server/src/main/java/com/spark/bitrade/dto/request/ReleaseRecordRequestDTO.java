package com.spark.bitrade.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *   DTO
 * @author lc
 * @since 2019/12/17
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseRecordRequestDTO {

    private static final long serialVersionUID = -7151685362718045801L;

    /**
     * @parm 用户id
     */
    @NotNull(message = "memberId不能为空")
    @Min(value = 1,message = "memberId异常")
    private Long memberId;

    /**
     * 释放类型
     */
    @NotNull(message = "释放类型不能为空")
    @ApiModelProperty(value = "释放类型(0锁仓释放，1冻结释放)")
    private Integer status;

    /**
     * @parm 币种名称
     */
    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String coinSymbol;


    /**
     * @parm  释放数量
     */
    @NotBlank(message = "释放数量不能为空")
    @ApiModelProperty(value = "释放数量")
    private String releaseAmount;

    /**
     * @parm  账户流水记录id
     */
    @NotBlank(message = "账户流水记录id不能为空")
    @ApiModelProperty(value = "账户流水记录id")
    private String refId;


    @Override
    public String toString() {
        return "ExchangeReleaseRequestDTO{" +
                "memberId=" + memberId +
                ", status=" + status +
                ", coinSymbol='" + coinSymbol + '\'' +
                ", releaseAmount='" + releaseAmount + '\'' +
                ", refId='" + refId + '\'' +
                '}';
    }


}
