package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.AwardTaskType;
import com.spark.bitrade.constant.ProcessStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币币交易-推荐人奖励任务表(ExchangeReleaseAwardTask)表实体类
 *
 * @author yangch
 * @since 2020-01-17 17:18:53
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易-推荐人奖励任务表")
public class ExchangeReleaseAwardTask {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 奖励币种
     */
    @ApiModelProperty(value = "奖励币种", example = "")
    private String awardSymbol;

    /**
     * 关联ID
     */
    @ApiModelProperty(value = "关联ID", example = "")
    private String refId;

    /**
     * 参考数量
     */
    @ApiModelProperty(value = "参考数量", example = "")
    private BigDecimal refAmount;

    /**
     * 被推荐人用户ID
     */
    @ApiModelProperty(value = "被推荐人用户ID", example = "")
    private Long inviteeId;

    /**
     * 奖励币数量
     */
    @ApiModelProperty(value = "奖励币数量", example = "")
    private BigDecimal amount;

    /**
     * 任务类型：0=手续费返佣、1=直推用户买币累计奖励
     */
    @ApiModelProperty(value = "任务类型：0=手续费返佣、1=直推用户买币累计奖励", example = "")
    private AwardTaskType type;

    /**
     * 奖励状态：0=未处理,1=处理中,2=已处理
     */
    @ApiModelProperty(value = "奖励状态：0=未处理,1=处理中,2=已处理", example = "")
    private ProcessStatus status;

    /**
     * 奖励到账时间
     */
    @ApiModelProperty(value = "奖励到账时间", example = "")
    private Date releaseTime;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", example = "")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新日期
     */
    @ApiModelProperty(value = "更新日期", example = "")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, update = "NOW()")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;


}