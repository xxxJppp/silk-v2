package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 14:35  
 */
@Data
public class SupportStreamSwitchVo extends AuditRecordVo {
    @ApiModelProperty(value = "项目方名称")
    private String name;
    @ApiModelProperty(value = "项目方币种")
    private String coin;
    @ApiModelProperty(value = "操作类型 1打开引流开关 0关闭引流开关")
    private Integer switchStatus;
    @ApiModelProperty(value = "操作类型 1打开引流开关 0关闭引流开关")
    private String switchStatusName;
}
