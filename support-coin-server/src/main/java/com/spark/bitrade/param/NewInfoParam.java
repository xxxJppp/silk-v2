package com.spark.bitrade.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.12 13:37
 */
@Data
public class NewInfoParam extends PageParam {

    @ApiModelProperty("标题")
    private String titile;

    @ApiModelProperty("更新开始时间yyyy-MM-dd")
    private String updateStartTime;

    @ApiModelProperty("更新开始时间yyyy-MM-dd")
    private String updateEndTime;

    @Override
    public void transTime(){

        if(!StringUtils.isEmpty(startTime)){
            this.setStartTime(startTime+" 00:00:00");
        }
        if(!StringUtils.isEmpty(endTime)){
            this.setEndTime(endTime+" 23:59:59");
        }
        if(!StringUtils.isEmpty(updateStartTime)){
            this.setUpdateStartTime(updateStartTime+" 00:00:00");
        }
        if(!StringUtils.isEmpty(updateEndTime)){
            this.setUpdateEndTime(updateEndTime+" 23:59:59");
        }
    }

}
