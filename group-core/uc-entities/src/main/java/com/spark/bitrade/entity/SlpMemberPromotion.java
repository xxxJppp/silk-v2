package com.spark.bitrade.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 会员推荐关系表(SlpMemberPromotion)表实体类
 *
 * @author wsy
 * @since 2019-06-20 10:00:05
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "会员推荐关系表")
public class SlpMemberPromotion {

    /**
     * 会员ID
     */
    @TableId
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 邀请人ID
     */
    @ApiModelProperty(value = "邀请人ID", example = "")
    private Long inviterId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 直推人数
     */
    @ApiModelProperty(value = "直推人数", example = "")
    private Integer directCount;

    /**
     * 总推荐人数
     */
    @ApiModelProperty(value = "总推荐人数", example = "")
    private Integer allCount;

    /**
     * 已经统计过 0 未统计，1已统计 （一个用户只能统计一次哦）
     */
    @ApiModelProperty(value = "已经统计过 （一个用户只能统计一次哦）", example = "")
    private Integer statisticed;
}