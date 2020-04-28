package com.spark.bitrade.vo;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 14:34  
 */
@Data
public class AuditRecordVo {

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "审核状态")
    private AuditStatusEnum auditStatus;

    @ApiModelProperty(value = "审核状态")
    private String auditStatusName;

    @ApiModelProperty(value = "审核意见")
    private String auditOpinion;

}
