package com.spark.bitrade.vo;

import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SectionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 14:34  
 */
@Data
public class UpCoinApplyRecordVo {

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String name;

    /**
     * 简介(对应国际化表key=userId+自定义唯一标识+language)
     */
    @ApiModelProperty(value = "简介")
    private String projectIntro;

    /**
     * 申请人ID
     */
    @ApiModelProperty(value = "申请人ID")
    private Long memberId;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String linkPerson;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String linkPhone;

    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    private String wechatNo;

    /**
     * 微信二维码url
     */
    @ApiModelProperty(value = "微信二维码url")
    private String wechatUrl;

    /**
     * 附件地址集合
     */
    @ApiModelProperty(value = "附件地址集合")
    private List nameUrls;

    /**
     * 项目交易码
     */
    @ApiModelProperty(value = "项目交易码")
    private String tradeCode;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String coin;

    /**
     * 申请上币板块
     */
    @ApiModelProperty(value = "申请上币板块")
    private SectionTypeEnum sectionType;

    /**
     * 引流开关默认关{0:关,1:开}
     */
    @ApiModelProperty(value = "引流开关默认关{0:关,1:开}")
    private Integer streamStatus;

    /**
     * 审核状态{0:待审核,1:审核通过,2:审核拒绝}
     */
    @ApiModelProperty(value = " 审核状态{0:待审核,1:审核通过,2:审核拒绝}")
    private AuditStatusEnum auditStatus;

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    /**
     * 审核人
     */
    @ApiModelProperty(value = "审核人")
    private Long auditId;

    /**
     * 审核意见
     */
    @ApiModelProperty(value = "审核意见")
    private String auditOpinion;

    /**
     * 是否删除{0:否,1:是}
     */
    @ApiModelProperty(value = "是否删除{0:否,1:是}")
    private BooleanEnum deleteFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "区号")
    private String areaCode;
}
