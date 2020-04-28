package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.CommunityApplyType;
import com.spark.bitrade.constant.SuperAuditStatus;
import com.spark.bitrade.entity.SuperPartnerApplyRecord;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
public interface SuperPartnerApplyRecordService extends IService<SuperPartnerApplyRecord> {

    /**
     * 查询用户申请加入合伙人的 申请记录
     */
    List<SuperPartnerApplyRecord> findByMemberId(Long memberId, CommunityApplyType applyType, SuperAuditStatus auditStatus);

    /**
     * 查询用户申请加入合伙人的 待处理或者已驳回的申请记录
     * @param memberId
     * @param applyType
     * @return
     */
    List<SuperPartnerApplyRecord> findByMemberId(Long memberId, CommunityApplyType applyType);

    /**
     * 查询合伙人最近的审批记录
     * @param memberId
     * @return
     */
    List<SuperPartnerApplyRecord> findByMemberId(Long memberId);
}
