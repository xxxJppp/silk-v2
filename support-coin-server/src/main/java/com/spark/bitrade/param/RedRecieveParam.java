package com.spark.bitrade.param;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class RedRecieveParam {


    @ApiModelProperty("页码")
    private Integer page=1;

    @ApiModelProperty("size")
    private Integer pageSize=10;

    @ApiModelProperty("领取开始时间yyyy-MM-dd")
    private String receiveStartTime;

    @ApiModelProperty("领取结束时间yyyy-MM-dd")
    private String receiveEndTime;

    @ApiModelProperty("领取状态领取状态{0:未领取1:已领取,2:已收回}")
    private Integer receiveStatus;



    public void transTime(){

        if(!StringUtils.isEmpty(receiveStartTime)){
            this.setReceiveStartTime(receiveStartTime+" 00:00:00");
        }
        if(!StringUtils.isEmpty(receiveEndTime)){
            this.setReceiveEndTime(receiveEndTime+" 23:59:59");
        }
    }


}
