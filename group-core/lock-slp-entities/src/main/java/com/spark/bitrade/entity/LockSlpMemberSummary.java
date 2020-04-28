package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员社区奖励实时统计表(LockSlpMemberSummary)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "会员社区奖励实时统计表")
public class LockSlpMemberSummary {

    /**
     * id=（会员ID+币种）
     */
    @TableId
    @ApiModelProperty(value = "id=（会员ID+币种）", example = "")
    private String id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种，必须大写
     */
    @ApiModelProperty(value = "币种，必须大写", example = "")
    private String coinUnit;

    /**
     * 社区节点ID
     */
    @ApiModelProperty(value = "社区节点ID", example = "")
    private Long currentLevelId;

    /**
     * 社区节点名称
     */
    @ApiModelProperty(value = "社区节点名称", example = "")
    private String currentLevelName;

    /**
     * 社区节点奖励比例
     */
    @ApiModelProperty(value = "社区节点奖励比例", example = "")
    private BigDecimal releaseRate;

    /**
     * 投入最大数量
     */
    @ApiModelProperty(value = "投入最大数量", example = "")
    private BigDecimal maxLockAmount;

    /**
     * 投入最大释放比例
     */
    @ApiModelProperty(value = "投入最大释放比例", example = "")
    private BigDecimal maxReleaseRate;

    /**
     * 投入最小数量
     */
    @ApiModelProperty(value = "投入最小数量", example = "")
    private BigDecimal minLockAmount;

    /**
     * 投入最小释放比例
     */
    @ApiModelProperty(value = "投入最小释放比例", example = "")
    private BigDecimal minReleaseRate;

    /**
     * 个人锁仓有效总币数
     */
    @ApiModelProperty(value = "个人锁仓有效总币数", example = "")
    private BigDecimal totalValidAmount;

    /**
     * 个人锁仓奖励基金有效总币数
     */
    @ApiModelProperty(value = "个人锁仓奖励基金有效总币数", example = "")
    private BigDecimal totalRemainAmount;

    /**
     * 子部门锁仓有效总币数
     */
    @ApiModelProperty(value = "子部门锁仓有效总币数", example = "")
    private BigDecimal totalSubValidAmount;

    /**
     * 推荐人ID
     */
    @ApiModelProperty(value = "推荐人ID", example = "")
    private Long inviterId;

    /**
     * 直接推荐人数
     */
    @ApiModelProperty(value = "直接推荐人数", example = "")
    private Integer promotion;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;


}