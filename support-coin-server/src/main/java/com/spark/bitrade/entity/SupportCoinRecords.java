package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 上币币种基本信息申请
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportCoinRecords implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人ID
     */
    private Long memberId;

    /**
     * 上币申请ID(support_up_coin_apply)
     */
    private Long upCoinId;

    /**
     * 简介(对应国际化表key=userId+自定义唯一标识+language)
     */
    private String introKey;

    /**
     * 联系方式,审核通过更新到上币申请表
     */
    private String linkPhone;

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
     * 区号
     */
    private String areaCode;
    
    public static final String MEMBER_ID = "member_id";

    public static final String UP_COIN_ID = "up_coin_id";

    public static final String CREATE_TIME = "create_time";

    public static final String AUDIT_STATUS = "audit_status";

}
