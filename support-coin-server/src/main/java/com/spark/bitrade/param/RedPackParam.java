package com.spark.bitrade.param;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class RedPackParam {

    @ApiModelProperty("红包名称")
    private String redPackName;

    @ApiModelProperty("红包状态 0:未开始 1:进行中 2:已结束")
    private Integer redStatus;

    @ApiModelProperty("审批状态 0:待审核 1:审核通过 2:审核不通过")
    protected AuditStatusEnum auditStatus;

    @ApiModelProperty("页码")
    protected Integer page=1;

    @ApiModelProperty("size")
    protected Integer pageSize=10;

    @ApiModelProperty("红包开始时间区间 开始时间yyyy-MM-dd")
    protected String startTimeFrom;

    @ApiModelProperty("红包开始时间区间 结束时间yyyy-MM-dd")
    protected String startTimeEnd;

    @ApiModelProperty("红包结束时间区间 开始时间yyyy-MM-dd")
    protected String endTimeFrom;

    @ApiModelProperty("红包结束时间区间 结束时间yyyy-MM-dd")
    protected String endTimeEnd;



    public void transTime(){

        if(!StringUtils.isEmpty(startTimeFrom)){
            this.setStartTimeFrom(startTimeFrom+" 00:00:00");
        }
        if(!StringUtils.isEmpty(startTimeEnd)){
            this.setStartTimeEnd(startTimeEnd+" 23:59:59");
        }

        if(!StringUtils.isEmpty(endTimeFrom)){
            this.setEndTimeFrom(endTimeFrom+" 00:00:00");
        }
        if(!StringUtils.isEmpty(endTimeEnd)){
            this.setEndTimeEnd(endTimeEnd+" 23:59:59");
        }

    }
}
