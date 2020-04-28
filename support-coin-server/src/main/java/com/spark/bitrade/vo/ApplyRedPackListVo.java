package com.spark.bitrade.vo;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ApplyRedPackListVo {
    @ApiModelProperty(value = "红包申请ID 重新提审时需传此ID,查询审批历史需传此ID")
    private Long id;
    @ApiModelProperty(value = "红包名称")
    private String redPackName;
    @ApiModelProperty(value = "红包ID")
    private Long redPackManageId;
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "红包币种")
    private String redCoin;
    @ApiModelProperty(value = "总金额")
    private BigDecimal redTotalAmount;
    @ApiModelProperty(value = "总份数")
    private Integer redTotalCount;
    @ApiModelProperty(value = "领取进度")
    private String receiveProgress;
    @ApiModelProperty(value = "领取模式{1:随机数量,2:固定数量}")
    private Integer receiveType;
    @ApiModelProperty(value = "领取对象{0:所有,1:新会员, 2:老会员}")
    private Integer isOldUser;
    @ApiModelProperty(value = "审批状态 0:待审核 1:审核通过 2:审核不通过")
    private AuditStatusEnum auditStatus;
    @ApiModelProperty(value = "审批意见")
    private String auditOpinion;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "提审时间")
    private Date applyTime;
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    @ApiModelProperty("红包状态 0:未开始 1:进行中 2:已结束")
    private Integer redStatus;
    @ApiModelProperty("红包url")
    private String url;

    private BigDecimal maxAmount;
    private BigDecimal minAmount;
}
