package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 扶持上币交易对 
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportCoinMatch implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人ID
     */
    @ApiModelProperty(value = "申请人ID")
    private Long memberId;

    /**
     * 上币申请ID(support_up_coin_apply)
     */
    @ApiModelProperty(value = "上币申请ID")
    private Long upCoinId;

    /**
     * 目标币种
     */
    @ApiModelProperty(value = "目标币种")
    private String targetCoin;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 审核状态{0:待审核,1:审核通过,2:审核拒绝}
     */
    @ApiModelProperty(value = "审核状态{0:待审核,1:审核通过,2:审核拒绝}")
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

    public static final String UP_COIN_ID = "up_coin_id";

    public static final String MEMBER_ID = "member_id";

    public static final String AUDIT_STATUS = "audit_status";

    public static final String TARGET_COIN = "target_coin";

    public static final String CREATE_TIME = "create_time";

}
