package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.DamagesCalcType;
import com.spark.bitrade.constant.DamagesCoinType;
import com.spark.bitrade.constant.LockCoinActivitieType;
import com.spark.bitrade.constant.LockSettingStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 锁仓活动方案配置(LockCoinActivitieSetting)表实体类
 *
 * @author zhangYanjun
 * @since 2019-06-19 14:08:14
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "锁仓活动方案配置")
public class LockCoinActivitieSetting {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 管理员id
     */
    @ApiModelProperty(value = "管理员id", example = "")
    private Long adminId;

    /**
     * 活动币种符号
     */
    @ApiModelProperty(value = "活动币种符号", example = "")
    private String coinSymbol;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 收益保障：固定返币数量
     */
    @ApiModelProperty(value = "收益保障：固定返币数量", example = "")
    private BigDecimal earningPerUnit;

    /**
     * 收益保障：最低年化率
     */
    @ApiModelProperty(value = "收益保障：最低年化率", example = "")
    private BigDecimal earningRate;

    /**
     * 活动生效时间（购买后立即生效，此字段为空）
     */
    @ApiModelProperty(value = "活动生效时间（购买后立即生效，此字段为空）", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveTime;

    /**
     * 活动截止时间
     */
    @ApiModelProperty(value = "活动截止时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 锁仓时长（单位：天）
     */
    @ApiModelProperty(value = "锁仓时长（单位：天）", example = "")
    private Integer lockDays;

    /**
     * 最大购买数量（币数、份数）
     */
    @ApiModelProperty(value = "最大购买数量（币数、份数）", example = "")
    private BigDecimal maxBuyAmount;

    /**
     * 最低购买数量（币数、份数）
     */
    @ApiModelProperty(value = "最低购买数量（币数、份数）", example = "")
    private BigDecimal minBuyAmount;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String name;

    /**
     * 活动备注
     */
    @ApiModelProperty(value = "活动备注", example = "")
    private String note;

    /**
     * 活动计划数量（币数、份数）
     */
    @ApiModelProperty(value = "活动计划数量（币数、份数）", example = "")
    private BigDecimal planAmount;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 活动状态 0未生效，1已生效，2已失效
     */
    @ApiModelProperty(value = "活动状态 0未生效，1已生效，2已失效", example = "")
    private LockSettingStatus status;

    /**
     * 活动类型，0整存整取，1锁币
     */
    @ApiModelProperty(value = "活动类型，0整存整取，1锁币", example = "")
    private LockCoinActivitieType type;

    /**
     * 活动每份数量（1表示1个币，大于1表示每份多少币）
     */
    @ApiModelProperty(value = "活动每份数量（1表示1个币，大于1表示每份多少币）", example = "")
    private BigDecimal unitPerAmount;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 关联活动方案ID
     */
    @ApiModelProperty(value = "关联活动方案ID", example = "")
    private Long activitieId;

    /**
     * 提前解锁：违约金数目
     */
    @ApiModelProperty(value = "提前解锁：违约金数目", example = "")
    private BigDecimal damagesAmount;

    /**
     * 提前解锁：违约金计算类型（百分比，固定数量）
     */
    @ApiModelProperty(value = "提前解锁：违约金计算类型（百分比，固定数量）", example = "")
    private DamagesCalcType damagesCalcType;

    /**
     * 提前解锁：违约金类型（币、人民币）
     */
    @ApiModelProperty(value = "提前解锁：违约金类型（币、人民币）", example = "")
    private DamagesCoinType damagesCoinType;

    /**
     * 活动参与数量（币数、份数）
     */
    @ApiModelProperty(value = "活动参与数量（币数、份数）", example = "")
    private BigDecimal boughtAmount;

    /**
     * 奖励系数（0~1）
     */
    @ApiModelProperty(value = "奖励系数（0~1）", example = "")
    private BigDecimal rewardFactor;

    /**
     * 开始释放
     */
    @ApiModelProperty(value = "开始释放", example = "")
    private Integer beginDays;

    /**
     * 每期天数
     */
    @ApiModelProperty(value = "每期天数", example = "")
    private Integer cycleDays;

    /**
     * 周期比例
     */
    @ApiModelProperty(value = "周期比例", example = "")
    private String cycleRatio;

    /**
     * 锁仓期数
     */
    @ApiModelProperty(value = "锁仓期数", example = "")
    private Integer lockCycle;


}