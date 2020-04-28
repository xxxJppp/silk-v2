package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 布朗计划，直推部门详情VO
 *
 * @author zhongxj
 * @date 2019.7.12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "布朗计划，直推部门详情VO")
public class PromotionMemberDetailsVO {
    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID", example = "")
    private Long memberId;
    /**
     * 当前节点
     */
    @ApiModelProperty(value = "当前节点", example = "")
    private String currentLevelName;
    /**
     * 实名信息
     */
    @ApiModelProperty(value = "实名信息", example = "")
    private String realName;
    /**
     * 邀请时间
     */
    @ApiModelProperty(value = "邀请时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date recommendTime;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobilePhone;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "")
    private String email;
    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date registrationTime;
    /**
     * 有效性（0-无效；1-有效）
     */
    @ApiModelProperty(value = "有效性（0-无效；1-有效）", example = "0")
    private Integer status;
}