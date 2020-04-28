package com.spark.bitrade.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @description: 有效用户查询DTO
 * @author lc
 * @since 2019/11/7 16:51
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ValidUserRequestDTO {


    /**
     *代币数量
     */
    @NotBlank(message = "代币不能为空")
    @ApiModelProperty(value = "代币")
    @Length(max = 128, message = "代币输入超长")
    private String token;

    /**
     * 有效用户数
     */
    private Integer validUserNum;

    /**
     * 需缴纳代币的数量
     */
    @NotBlank(message = "代币数量不能为空")
    @ApiModelProperty(value = "代币数量")
    private String number;




}
