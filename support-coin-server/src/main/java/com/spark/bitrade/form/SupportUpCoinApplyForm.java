package com.spark.bitrade.form;

import com.spark.bitrade.constant.SectionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 * 扶持上币项目方主表
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportUpCoinApplyForm implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 项目名称
     */
    @NotBlank
    @ApiModelProperty(value = "项目名称")
    private String name;

    /**
     * 简介中文
     */
    @NotBlank
    @ApiModelProperty(value = "项目简介中文")
    private String projectIntro;
    /**
     * 联系人
     */
    @NotBlank
    @ApiModelProperty(value = "联系人")
    private String linkPerson;

    /**
     * 联系电话
     */
    @NotBlank
    @ApiModelProperty(value = "联系电话")
    private String linkPhone;

    /**
     * 微信号
     */
    @NotBlank
    @ApiModelProperty(value = "微信号")
    private String wechatNo;

    /**
     * 微信二维码url
     */
    @NotBlank
    @ApiModelProperty(value = "微信二维码url")
    private String wechatUrl;

    /**
     * 附件地址集合逗号隔开
     */
    @ApiModelProperty(value = "附件地址集合逗号隔开")
    private String[] attchArray;
    /**
     * 币种
     */
    @NotBlank
    @Pattern(regexp = "^[A-Za-z]+$")
    @Length(max = 10)
    @ApiModelProperty(value = "币种")
    private String coin;

    /**
     * 申请上币板块
     */
    @NotNull
    @ApiModelProperty(value = "申请上币板块")
    private SectionTypeEnum sectionType;

    /**
     * 区号
     */
    @ApiModelProperty(value = "区号")
    @NotBlank
    private String areaCode;
}
