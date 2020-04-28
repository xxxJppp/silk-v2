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
 * 
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportMemberMenu implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 申请用户id
     */
    private Long memberId;

    /**
     * 审核状态{0:待审核,1:审核通过,2:审核拒绝}
     */
    private AuditStatusEnum auditStatus;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 审核人id
     */
    private Long auditId;

    /**
     * 使用到期时间
     */
    private Date dueTime;

    /**
     * 有效状态{0:有效,1:失效}
     */
    private Integer effectiveStatus;

    /**
     * 永久有效状态{0:永久有效,1:非永久有效}
     */
    private Integer alwaysStatus;

    /**
     * 是否删除{0:否,1:是}
     */
    private BooleanEnum deleteFlag;

    /**
     * 创建人
     */
    private Long createUserid;

    /**
     * 修改人
     */
    private Long modifyUserid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
