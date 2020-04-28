package com.spark.bitrade.param;

import com.spark.bitrade.constant.SectionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.11 09:24  
 */
@Data
public class SectionSearchParam extends PageParam {

    @ApiModelProperty("当前版块")
    private SectionTypeEnum currentSection;

    @ApiModelProperty("目标版块")
    private SectionTypeEnum targetSection;


}
