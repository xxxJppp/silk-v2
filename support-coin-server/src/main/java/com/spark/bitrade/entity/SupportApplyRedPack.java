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
 * 红包申请表
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportApplyRedPack implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 红包ID
     */
    private Long redPackManageId;

    /**
     * 红包名称
     */
    private String redPackName;

    /**
     * 红包开始时间
     */
    private Date startTime;

    /**
     * 红包结束时间
     */
    private Date endTime;

    /**
     * 红包币种
     */
    private String redCoin;

    /**
     * 总金额
     */
    private BigDecimal redTotalAmount;

    /**
     * 红包最大值
     */
    private BigDecimal maxAmount;

    /**
     * 红包最小值
     */
    private BigDecimal minAmount;

    /**
     * 总份数
     */
    private Integer redTotalCount;

    /**
     * 领取模式{1:随机数量,2:固定数量}
     */
    private Integer receiveType;

    /**
     * 领取对象{0:所有,1:新会员, 2:老会员}
     */
    private Integer isOldUser;

    /**
     * 提审时间(随着记录更新)
     */
    private Date applyTime;

    /**
     * 审核时间(随着记录更新)
     */
    private Date auditTime;

    /**
     * 手续费率
     */
    private BigDecimal serviceCharge;

    /**
     * 审核意见(随着记录更新)
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
     * 红包时限
     */
    private Integer within;

}
