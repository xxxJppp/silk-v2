package com.spark.bitrade.vo;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ApplyRedPackAuditRecordVo {
    @ApiModelProperty(value = "创建时间 提审时间")
    private Date createTime;
    @ApiModelProperty(value = "审批时间")
    private Date updateTime;
    @ApiModelProperty(value = "审批人")
    private String auditPerson;
    @ApiModelProperty(value = "审批状态 0:待审核 1:审核通过 2:审核不通过")
    private AuditStatusEnum auditStatus;
    @ApiModelProperty(value = "审核备注 /审批意见")
    private String auditOpinion;
    @ApiModelProperty(value = "支付币种")
    private String payCoin;
    @ApiModelProperty(value = "支付总费用")
    private BigDecimal payAmount;
    @ApiModelProperty(value = "基础支付费用")
    private BigDecimal baseAmount;
    @ApiModelProperty("追加支付费用")
    private BigDecimal addAmount;
}
