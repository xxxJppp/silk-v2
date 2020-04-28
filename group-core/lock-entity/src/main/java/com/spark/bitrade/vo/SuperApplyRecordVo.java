package com.spark.bitrade.vo;

import com.spark.bitrade.constant.CommunityApplyType;
import com.spark.bitrade.constant.SuperAuditStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.17 09:37  
 */
@Data
public class SuperApplyRecordVo {

    /**
     * 审批状态
     */
    @ApiModelProperty(value = "审批状态 0待处理 1已通过 2已驳回", example = "")
    private SuperAuditStatus auditStatus;

    /**
     * 审批人id
     */
    @ApiModelProperty(value = "审批人id", example = "")
    private Long auditMember;

    /**
     * 审批意见
     */
    @ApiModelProperty(value = "审批意见", example = "")
    private String opinion;

    /**
     * 审批时间
     */
    @ApiModelProperty(value = "审批时间", example = "")
    private Date auditTime;

    /**
     * 社区id
     */
    @ApiModelProperty(value = "社区id", example = "")
    private Long communityId;


    /**
     * 申请类型
     */
    @ApiModelProperty(value = " 申请类型  0申请合伙人 1退出合伙人 2加入社区 3退出社区", example = "")
    private CommunityApplyType applyType;


    /**
     * 原因备注
     */
    @ApiModelProperty(value = " 报名原因", example = "")
    private String remark;

    @ApiModelProperty(value = " 锁仓数量", example = "")
    private BigDecimal lockAmount;

    @ApiModelProperty(value = " 锁仓币种", example = "")
    private String coinUnit;

}
