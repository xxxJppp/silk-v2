package com.spark.bitrade.form;

import com.spark.bitrade.constant.SectionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 15:52  
 */
@Data
public class ChangeSectionForm {

    /**
     * 目标版块
     */
    @ApiModelProperty(value = "目标版块")
    @NotNull
    private SectionTypeEnum targetSection;

    @ApiModelProperty(value = "支付币种")
    @NotBlank
    private String payCoin;


    @ApiModelProperty(value = "备注")
    private String remark;
}
