package com.spark.bitrade.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.11 10:25  
 */
@Data
public class StreamSearchParam extends PageParam {

    @ApiModelProperty("操作内容0:关闭引流开关,1:打开引流开关")
    private Integer switchStatus;

}
