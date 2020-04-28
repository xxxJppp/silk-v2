package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SectionTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 扶持上币项目方主表
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportUpCoinApply implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 简介(对应国际化表key=userId+自定义唯一标识+language)
     */
    private String introKey;

    /**
     * 申请人ID
     */
    private Long memberId;

    /**
     * 联系人
     */
    private String linkPerson;

    /**
     * 联系电话
     */
    private String linkPhone;

    /**
     * 微信号
     */
    private String wechatNo;

    /**
     * 微信二维码url
     */
    private String wechatUrl;

    /**
     * 附件地址集合逗号隔开
     */
    private String attchUrls;

    /**
     * 项目交易码
     */
    private String tradeCode;

    /**
     * 币种
     */
    private String coin;

    /**
     * 申请上币板块
     */
    private SectionTypeEnum sectionType;

    /**
     * 实际上币板块
     */
    private SectionTypeEnum realSectionType;
    /**
     * 引流开关默认关{0:关,1:开}
     */
    private Integer streamStatus;

    /**
     * 审核状态{0:待审核,1:审核通过,2:审核拒绝}
     */
    private AuditStatusEnum auditStatus;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核人
     */
    private Long auditId;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 是否删除{0:否,1:是}
     */
    private BooleanEnum deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
    /**
     * 项目介绍key
     */
    private  String projectIntroKey;
    /**
     * 区号
     */
    private String areaCode;
}
