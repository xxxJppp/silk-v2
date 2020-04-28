package com.spark.bitrade.dto;

import com.spark.bitrade.constant.IncubatorsBasicStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 孵化区-上币申请表
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IncubatorsBasicInformationDto implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty(value = "ID", example = "")
    private Long id;
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", example = "")
    private String proName;
    /**
     * 社区名称
     */
    @ApiModelProperty(value = "社区名称", example = "")
    private String communityName;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人", example = "")
    private String contactPerson;
    /**
     * 项目简介
     */
    @ApiModelProperty(value = "项目简介", example = "")
    private String proDesc;
    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", example = "")
    private String telPhone;
    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号", example = "")
    private String wechatNum;
    /**
     * 上传微信二维码地址
     */
    @ApiModelProperty(value = "上传微信二维码地址", example = "")
    private String wechatCode;
    /**
     * 总锁仓（SLU）
     */
    @ApiModelProperty(value = "总锁仓（SLU）", example = "")
    private BigDecimal lockUpNum;
    /**
     * 已锁仓币种
     */
    @ApiModelProperty(value = "已锁仓币种", example = "")
    private String lockCoinId;
    /**
     * 申请会员ID
     */
    @ApiModelProperty(value = "申请会员ID", example = "")
    private Long memberId;
    /**
     * 状态（0-上币待审核；1-上币审核通过；2-上币审核拒绝）
     */
    @ApiModelProperty(value = "状态（0-上币待审核；1-上币审核通过；2-上币审核拒绝）", example = "")
    private IncubatorsBasicStatus status;
    /**
     * 审核意见
     */
    @ApiModelProperty(value = "审核意见", example = "")
    private String auditOpinion;
    /**
     * 升仓数量
     */
    @ApiModelProperty(value = "升仓数量", example = "")
    private BigDecimal num;
    /**
     * 升仓币种
     */
    @ApiModelProperty(value = "升仓币种", example = "")
    private String coinId;
    /**
     * 是否发起升仓（true-发起；false-未发起）
     */
    @ApiModelProperty(value = "是否发起升仓（true-发起；false-未发起）", example = "")
    private boolean liftStatus;
}
