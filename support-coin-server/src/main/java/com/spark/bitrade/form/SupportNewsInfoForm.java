package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 16:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportNewsInfoForm implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("组ID")
    private String  groupId;
    /**
     * 标题
     */
    @NotBlank
    @ApiModelProperty(value = "资讯名称")
    private String title;

    /**
     * 内容
     */
    @NotBlank
    @ApiModelProperty(value = "资讯内容")
    private String text;

    /**
     * 申请人ID
     */
    @NotBlank
    @ApiModelProperty(value = "用户id")
    private String memberId;

//    private String coinName;
}
