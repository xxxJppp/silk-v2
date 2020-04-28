package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.LockRewardSatus;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.SmsSendStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 锁仓记录(LockCoinDetail)表实体类
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:34:08
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "锁仓记录")
public class LockCoinDetail {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 撤销时间
     */
    @ApiModelProperty(value = "撤销时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancleTime;

    /**
     * 活动币种
     */
    @ApiModelProperty(value = "活动币种", example = "")
    private String coinUnit;

    /**
     * 锁仓价格相对USDT
     */
    @ApiModelProperty(value = "锁仓价格相对USDT", example = "")
    private BigDecimal lockPrice;

    /**
     * 锁仓时间
     */
    @ApiModelProperty(value = "锁仓时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lockTime;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 预计收益
     */
    @ApiModelProperty(value = "预计收益", example = "")
    private BigDecimal planIncome;

    /**
     * 计划解锁时间
     */
    @ApiModelProperty(value = "计划解锁时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planUnlockTime;

    /**
     * 关联活动ID
     */
    @ApiModelProperty(value = "关联活动ID", example = "")
    private Long refActivitieId;

    /**
     * 剩余锁仓币数
     */
    @ApiModelProperty(value = "剩余锁仓币数", example = "")
    private BigDecimal remainAmount;

    /**
     * 状态（0已锁定、1已解锁、2已撤销、3解锁中）
     */
    @ApiModelProperty(value = "状态（0已锁定、1已解锁、2已撤销、3解锁中）", example = "")
    private LockStatus status;

    /**
     * 总锁仓币数
     */
    @ApiModelProperty(value = "总锁仓币数", example = "")
    private BigDecimal totalAmount;

    /**
     * 锁仓类型（0商家保证金、1员工锁仓、2锁仓活动、3理财锁仓、4SLB节点产品、5STO锁仓、6STO增值计划、7IEO锁仓、8金钥匙活动
     */
    @ApiModelProperty(value = "锁仓类型（0商家保证金、1员工锁仓、2锁仓活动、3理财锁仓、4SLB节点产品、5STO锁仓、6STO增值计划、7IEO锁仓、8金钥匙活动", example = "")
    private LockType type;

    /**
     * 解锁时间
     */
    @ApiModelProperty(value = "解锁时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date unlockTime;

    /**
     * 锁仓总金额（CNY）
     */
    @ApiModelProperty(value = "锁仓总金额（CNY）", example = "")
    private BigDecimal totalcny;

    /**
     * USDT价格（CNY）
     */
    @ApiModelProperty(value = "USDT价格（CNY）", example = "")
    private BigDecimal usdtPricecny;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;

    /**
     * 状态（不返佣、未返佣、已返佣）
     */
    @ApiModelProperty(value = "状态（不返佣、未返佣、已返佣）", example = "")
    private LockRewardSatus lockRewardSatus;

    /**
     * 短信发送状态(0:未发送,1:已发送,2:发送失败)
     */
    @ApiModelProperty(value = "短信发送状态(0:未发送,1:已发送,2:发送失败)", example = "")
    private SmsSendStatus smsSendStatus=SmsSendStatus.NO_SMS_SEND;

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