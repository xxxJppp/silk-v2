package com.spark.bitrade.vo;

import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.EquityStatus;
import com.spark.bitrade.constant.MemberCurrentJoinStatus;
import com.spark.bitrade.constant.SuperRegistration;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 合伙人用户关联表 即社区表
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(description = "社区VO")
public class SuperPartnerCommunityVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @ApiModelProperty(value = "社区ID", example = "")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户真实名称", example = "")
    private String realName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 社区名称
     */
    @ApiModelProperty(value = "社区名称", example = "")
    private String communityName;

    /**
     * 微信二维码地址
     */
    @ApiModelProperty(value = "微信二维码地址", example = "")
    private String wechatCode;

    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号", example = "")
    private String wechatNum;

    /**
     * 社区人数
     */
    @ApiModelProperty(value = "社区总人数", example = "")
    private Integer peopleCount;

    /**
     * 社区链接
     */
    @ApiModelProperty(value = "社区链接", example = "")
    private String communityLink;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "社区创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 退出合伙人时间
     */
    @ApiModelProperty(value = "合伙人退出时间", example = "")
    private Date exitTime;

    /**
     * 社区成立时间
     */
    @ApiModelProperty(value = "社区成立时间", example = "")
    private Date foundingTime;

    /**
     * 是否有效
     */
    @ApiModelProperty(value = "是否有效", example = "")
    private BooleanEnum usable;

    /**
     * 权益状态
     */
    @ApiModelProperty(value = "权益状态 0正常 1禁用权益", example = "")
    private EquityStatus equityStatus;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;

    /**
     * 注册来源 默认0 主动注册
     */
    @ApiModelProperty(value = "0主动注册 1孵化区注册 不可退出合伙人", example = "")
    private SuperRegistration sourceChannel=SuperRegistration.INITIATIVE;

    @ApiModelProperty(value = "0未加入社区 1当前社区成员 2其他社区成员", example = "")
    private MemberCurrentJoinStatus currentJoinStatus;

    @ApiModelProperty(value = "加入社区是否大于30天 只有查询自己加入的社区值才有效", example = "")
    private BooleanEnum ifMorethan30=BooleanEnum.IS_FALSE;
}
