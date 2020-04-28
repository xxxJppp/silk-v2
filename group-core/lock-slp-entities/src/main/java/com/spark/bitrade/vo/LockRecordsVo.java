package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SLP加速释放页面，锁仓记录VO
 *
 * @author zhongxj
 * @since 2019-07-10 09:27:03
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "锁仓记录VO")
public class LockRecordsVo {
    /**
     * 直推部门ID
     */
    @ApiModelProperty(value = "直推部门ID", example = "")
    private Long refInviteesId;

    /**
     * 锁仓类型（1-锁仓、2-团队锁仓）
     */
    @ApiModelProperty(value = "锁仓类型（1-锁仓、2-团队锁仓）", example = "")
    private Integer type;

    /**
     * 锁仓数量
     */
    @ApiModelProperty(value = "锁仓数量", example = "100")
    private BigDecimal lockAmount;

    /**
     * 状态（3=加仓完成，显示：planName 因升仓而作废；其余状态，如果refPrevPlanName不为空，则显示：refPrevPlanName 升仓为 planName，反之显示planName即可）
     */
    @ApiModelProperty(value = "状态（3=加仓完成，显示：planName 因升仓而作废；其余状态，如果refPrevPlanName不为空，则显示：refPrevPlanName 升仓为 planName，反之显示planName即可）", example = "")
    private int status;

    /**
     * 理财套餐
     */
    @ApiModelProperty(value = "理财套餐", example = "套餐A")
    private String planName;

    /**
     * 关联的前一条套餐名称
     */
    @ApiModelProperty(value = "关联的前一条套餐名称", example = "")
    private String refPrevPlanName;

    /**
     * 锁仓时间
     */
    @ApiModelProperty(value = "锁仓时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;

}
