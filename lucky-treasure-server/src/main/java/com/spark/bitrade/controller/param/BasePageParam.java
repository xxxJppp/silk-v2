package com.spark.bitrade.controller.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class BasePageParam {

    @ApiModelProperty("页码")
    protected Integer page=1;

    @ApiModelProperty("size")
    protected Integer pageSize=10;

    @ApiModelProperty("开始时间yyyy-MM-dd")
    protected String startTime;

    @ApiModelProperty("结束时间yyyy-MM-dd")
    protected String endTime;

    public void transTime(){

        if(!StringUtils.isEmpty(startTime)){
            this.setStartTime(startTime+" 00:00:00");
        }
        if(!StringUtils.isEmpty(endTime)){
            this.setEndTime(endTime+" 23:59:59");
        }
    }
}
