package com.spark.bitrade.vo;

import com.spark.bitrade.constant.SectionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 11:20  
 */
@Data
public class SupportSectionManageVo extends AuditRecordVo{

    @ApiModelProperty(value = "项目方")
    private String name;

    @ApiModelProperty(value = "当前版块")
    private SectionTypeEnum currentSection;

    @ApiModelProperty(value = "当前版块")
    private String currentSectionName;

    @ApiModelProperty(value = "目标版块")
    private SectionTypeEnum targetSection;

    @ApiModelProperty(value = "当前版块")
    private String targetSectionName;




}
