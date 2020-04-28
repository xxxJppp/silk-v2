package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.CommunityApplyType;
import com.spark.bitrade.constant.SuperAuditStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SuperPartnerApplyRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 申请类型
     */
    private CommunityApplyType applyType;

    /**
     * 社区id
     */
    private Long communityId;

    /**
     * 原因备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 审批状态
     */
    private SuperAuditStatus auditStatus;

    /**
     * 审批人id
     */
    private Long auditMember;

    /**
     * 审批意见
     */
    private String opinion;

    /**
     * 审批时间
     */
    private Date auditTime;


    public static final String ID = "id";

    public static final String MEMBER_ID = "member_id";

    public static final String APPLY_TYPE = "apply_type";

    public static final String COMMUNITY_ID = "community_id";

    public static final String REMARK = "remark";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String AUDIT_STATUS = "audit_status";

    public static final String AUDIT_MEMBER = "audit_member";

    public static final String OPINION = "opinion";

    public static final String AUDIT_TIME = "audit_time";

}
