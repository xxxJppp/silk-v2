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
 * 引流开关记录
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportStreamRecords implements Serializable {

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
     * 操作内容,审核通过时更新状态{0:关闭引流开关,1:打开引流开关}
     */
    private Integer switchStatus;

    /**
     * 备注
     */
    private String remark;

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
     * 操作类型{0:用户申请,1:手动操作}
     */
    private Integer operateType;
}
