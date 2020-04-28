package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.AuditStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 红包开通申请表
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportOpenRedPack implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目方币种
     */
    private String projectCoin;

    /**
     * 审核状态{0:待审核,1:审核通过,2:审核不通过}
     */
    @ApiModelProperty(value = "审核状态{0:待审核,1:审核通过,2:审核不通过}")
    private AuditStatusEnum auditStatus;

    /**
     * 审批意见
     */
    @ApiModelProperty(value = "审核意见")
    private String auditOpinion;
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


}
