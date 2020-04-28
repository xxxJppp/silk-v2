package com.spark.bitrade.form;

import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RedPackForm {
    /**
     * 红包名称
     */
    @NotBlank
    @ApiModelProperty("红包名称")
    private String redPackName;

    /**
     * 红包开始时间
     */
    @ApiModelProperty("红包开始时间")
    @NotBlank
    private String startTime;

    /**
     * 红包结束时间
     */
    @ApiModelProperty("红包结束时间")
    @NotBlank
    private String endTime;

    /**
     * 红包币种
     */
    @NotBlank
    @ApiModelProperty("红包币种")
    private String redCoin;

    /**
     * 总金额
     */
    @NotNull
    @ApiModelProperty("总金额")
    private BigDecimal redTotalAmount;

    /**
     * 红包最大值
     */
    @ApiModelProperty("红包最大值")
    private BigDecimal maxAmount;

    /**
     * 红包最小值
     */
    @ApiModelProperty("红包最小值")
    private BigDecimal minAmount;

    /**
     * 总份数
     */
    @ApiModelProperty("总份数")
    private Integer redTotalCount;

    /**
     * 领取模式{1:随机数量,2:固定数量}
     */
    @ApiModelProperty("领取模式{1:随机数量,2:固定数量}")
    private Integer receiveType;

    /**
     * 领取对象{0:所有,1:新会员, 2:老会员}
     */
    @ApiModelProperty("领取对象{0:所有,1:新会员, 2:老会员}")
    private Integer isOldUser=0;

    /**
     * 资金密码
     */
    @ApiModelProperty("资金密码")
    @NotBlank
    private String moneyPassword;

    @ApiModelProperty("红包申请id,重新提审需传此ID")
    private Long oldApplyRedPackId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("红包时限,默认1 ")
    private Integer within=12;
}
