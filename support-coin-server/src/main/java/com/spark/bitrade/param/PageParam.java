package com.spark.bitrade.param;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.11 09:19  
 */
@Data
public class PageParam {

    @ApiModelProperty("页码")
    protected Integer page=1;

    @ApiModelProperty("size")
    protected Integer pageSize=10;

    @ApiModelProperty("提交开始时间yyyy-MM-dd")
    protected String startTime;

    @ApiModelProperty("提交结束时间yyyy-MM-dd")
    protected String endTime;

    @ApiModelProperty("审批状态")
    protected AuditStatusEnum auditStatus;

    public void transTime(){

        if(!StringUtils.isEmpty(startTime)){
            this.setStartTime(startTime+" 00:00:00");
        }
        if(!StringUtils.isEmpty(endTime)){
            this.setEndTime(endTime+" 23:59:59");
        }
    }
}
