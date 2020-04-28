package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.IncubatorsOpType;
import com.spark.bitrade.constant.SuperAuditStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 孵化区-申请审核表
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IncubatorsApplicationReview implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增长ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请项目ID（外键，与incubators_basic_information表ID相关联）
     */
    private Long incubatorsId;

    /**
     * 申请会员ID
     */
    private Long memberId;

    /**
     * 项目名称
     */
    private String proName;

    /**
     * 申请类型（0-上币申请；1-退出）
     */
    private IncubatorsOpType operationType;

    /**
     * 项目简介
     */
    private String proDesc;

    /**
     * 理由
     */
    private String reason;

    /**
     * 状态（0-待审核；1-审核通过；2-审核拒绝）
     */
    private SuperAuditStatus status=SuperAuditStatus.PENDING;

    /**
     * 审核人ID
     */
    private Long operationId;

    /**
     * 审核人姓名
     */
    private String operationName;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近一次修改时间
     */
    private Date updateTime;


    public static final String ID = "id";

    public static final String INCUBATORS_ID = "incubators_id";

    public static final String MEMBER_ID = "member_id";

    public static final String PRO_NAME = "pro_name";

    public static final String OPERATION_TYPE = "operation_type";

    public static final String PRO_DESC = "pro_desc";

    public static final String REASON = "reason";

    public static final String STATUS = "status";

    public static final String OPERATION_ID = "operation_id";

    public static final String OPERATION_NAME = "operation_name";

    public static final String AUDIT_OPINION = "audit_opinion";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
