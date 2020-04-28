package com.spark.bitrade.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 *    
 *  @author Zhong Jiang  
 *  @date: 2019-11-19 9:24 
 */
@Data
public class PageParam {

    @ApiModelProperty("页码")
    protected Integer page=1;

    @ApiModelProperty("size")
    protected Integer pageSize=10;

    @ApiModelProperty("查询开始时间yyyy-MM-dd")
    protected String startTime;

    @ApiModelProperty("查询结束时间yyyy-MM-dd")
    protected String endTime;

    public void transTime(){

        if(StringUtils.hasText(startTime)){
            this.setStartTime(startTime+" 00:00:00");
        }
        if(StringUtils.hasText(endTime)){
            this.setEndTime(endTime+" 23:59:59");
        }
    }
}
