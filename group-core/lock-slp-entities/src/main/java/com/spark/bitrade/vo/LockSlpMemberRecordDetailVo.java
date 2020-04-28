package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  布朗计划，参与记录
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.09 14:33  
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "布朗计划用户参与记录详情")
@Builder
public class LockSlpMemberRecordDetailVo {

    /**
     * 套餐名称
     */
    @ApiModelProperty(value = "套餐名称", example = "套餐A")
    private String planName;

    /**
     * 投入
     */
    @ApiModelProperty(value = "投入", example = "2000.00")
    private BigDecimal lockAmount;

    /**
     * 参与时间
     */
    @ApiModelProperty(value = "参与时间", example = "2019-11-12 21:22")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;

    /**
     * 每日释放数量
     */
    @ApiModelProperty(value = "每日释放数量", example = "1.00")
    private BigDecimal dailyReleaseAmount;

    /**
     * 收益
     */
    @ApiModelProperty(value = "收益", example = "200.00")
    private BigDecimal planIncome;

    /**
     * 状态（3=加仓完成，显示：planName 因升仓而作废；其余状态，如果refPrevPlanName不为空，则显示：refPrevPlanName 升仓为 planName，反之显示planName即可）
     */
    @ApiModelProperty(value = "状态（3=加仓完成，显示：planName 因升仓而作废；其余状态，如果refPrevPlanName不为空，则显示：refPrevPlanName 升仓为 planName，反之显示planName即可）", example = "")
    private int status;

    /**
     * 关联的前一条套餐名称
     */
    @ApiModelProperty(value = "关联的前一条套餐名称", example = "")
    private String refPrevPlanName;
}
