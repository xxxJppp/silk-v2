package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.AuditStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 红包审核记录表
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportRedAuditRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 开头申请id
     */
    private Long openRedId;

    /**
     * 申请类型{0:开通申请,1:红包申请,2:优先级申请}
     */
    private Integer applyType;

    /**
     * 支付币种
     */
    private String payCoin;

    /**
     * 支付数量
     */
    private BigDecimal payAmount;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 项目方币种
     */
    private String projectCoin;

    /**
     * 审核状态{0:待审核,1:审核通过,2:审核不通过}
     */
    private AuditStatusEnum auditStatus;

    /**
     * 上币申请ID
     */
    private Long upCoinId;

    /**
     * 申请人ID
     */
    private Long memberId;

    /**
     * 是否删除{0:否,1:是}
     */
    private Integer deleteFlag;

    /**
     * 备注
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
     * 审批人ID
     */
    private Long auditId;

}
