//package com.spark.bitrade.entity;
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * 币币钱包每日快照表(ExchangeWalletSnapshoot)表实体类
// *
// * @author yangch
// * @since 2019-09-26 11:49:33
// */
//@SuppressWarnings("serial")
//@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
//@ApiModel(description = "币币钱包每日快照表")
//public class ExchangeWalletSnapshoot {
//
//    /**
//     * ID
//     */
//    @TableId
//    @ApiModelProperty(value = "ID", example = "")
//    private Long id;
//
//    /**
//     * 数据周期
//     */
//    @ApiModelProperty(value = "数据周期", example = "")
//    private Integer opTime;
//
//    /**
//     * 会员ID
//     */
//    @ApiModelProperty(value = "会员ID", example = "")
//    private Long memberId;
//
//    /**
//     * 币种
//     */
//    @ApiModelProperty(value = "币种", example = "")
//    private String coinUnit;
//
//    /**
//     * 上一期余额
//     */
//    @ApiModelProperty(value = "上一期余额", example = "")
//    private BigDecimal prevBalance;
//
//    /**
//     * 上一期冻结余额
//     */
//    @ApiModelProperty(value = "上一期冻结余额", example = "")
//    private BigDecimal prevFrozenBalance;
//
//    /**
//     * 当前余额
//     */
//    @ApiModelProperty(value = "当前余额", example = "")
//    private BigDecimal balance;
//
//    /**
//     * 当前冻结余额
//     */
//    @ApiModelProperty(value = "当前冻结余额", example = "")
//    private BigDecimal frozenBalance;
//
//    /**
//     * 当日交易余额
//     */
//    @ApiModelProperty(value = "当日交易余额", example = "")
//    private BigDecimal sumBalance;
//
//    /**
//     * 当日交易冻结余额
//     */
//    @ApiModelProperty(value = "当日交易冻结余额", example = "")
//    private BigDecimal sumFrozenBalance;
//
//    /**
//     * 汇率（USDT）
//     */
//    @ApiModelProperty(value = "汇率（USDT）", example = "")
//    private BigDecimal rate;
//
//    /**
//     * 当日累计增加的交易余额
//     */
//    @ApiModelProperty(value = "当日累计增加的交易余额", example = "")
//    private BigDecimal cumsumBalance;
//
//    /**
//     * 平衡关系状态：0-未知，1-满足平衡关系，2-不满足平衡关系
//     */
//    @ApiModelProperty(value = "平衡关系状态：0-未知，1-平衡，2-不平衡", example = "")
//    private Integer checkStatus;
//
//    /**
//     * 快照时间
//     */
//    @ApiModelProperty(value = "快照时间", example = "")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
//    private Date snapshootTime;
//
//    /**
//     * 创建日期
//     */
//    @ApiModelProperty(value = "创建日期", example = "")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
//    @TableField(value = "create_time", fill = FieldFill.INSERT)
//    private Date createTime;
//}