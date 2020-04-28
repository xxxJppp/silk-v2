package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.CommunityApplyType;
import com.spark.bitrade.constant.SuperAuditStatus;
import com.spark.bitrade.entity.SuperPartnerApplyRecord;
import com.spark.bitrade.mapper.SuperPartnerApplyRecordMapper;
import com.spark.bitrade.service.SuperPartnerApplyRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Service
public class SuperPartnerApplyRecordServiceImpl extends ServiceImpl<SuperPartnerApplyRecordMapper, SuperPartnerApplyRecord> implements SuperPartnerApplyRecordService {


    /**
     * 查询用户申请加入合伙人的 申请记录
     */
//    @Cacheable(cacheNames = "SuperPartnerApplyRecords",
//    key = "'list:superPartnerapplyRecord:memberId'+#memberId+':applyType'+#applyType.ordinal()+':auditStatus:'+#auditStatus.ordinal()")
    public List<SuperPartnerApplyRecord> findByMemberId(Long memberId, CommunityApplyType applyType, SuperAuditStatus auditStatus){
        QueryWrapper<SuperPartnerApplyRecord> rw=new QueryWrapper<>();
        rw.eq(SuperPartnerApplyRecord.MEMBER_ID,memberId).eq(SuperPartnerApplyRecord.APPLY_TYPE,applyType.getOrdinal())
                .eq(SuperPartnerApplyRecord.AUDIT_STATUS,auditStatus.getOrdinal())
                .orderByDesc(SuperPartnerApplyRecord.CREATE_TIME);

        return this.list(rw);
    }


    /**
     * 查询用户申请加入合伙人的 申请记录
     */
//    @Cacheable(cacheNames = "SuperPartnerApplyRecords",
//            key = "'list:superPartnerapplyRecord:memberId'+#memberId+':applyType:'+#applyType.ordinal()")
    public List<SuperPartnerApplyRecord> findByMemberId(Long memberId, CommunityApplyType applyType){
        QueryWrapper<SuperPartnerApplyRecord> rw=new QueryWrapper<>();
        rw.eq(SuperPartnerApplyRecord.MEMBER_ID,memberId).eq(SuperPartnerApplyRecord.APPLY_TYPE,applyType.getOrdinal())
                .orderByDesc(SuperPartnerApplyRecord.CREATE_TIME);

        return this.list(rw);
    }


    public List<SuperPartnerApplyRecord> findByMemberId(Long memberId){
        QueryWrapper<SuperPartnerApplyRecord> rw=new QueryWrapper<>();
        rw.eq(SuperPartnerApplyRecord.MEMBER_ID,memberId)
                .in(SuperPartnerApplyRecord.APPLY_TYPE,CommunityApplyType.APPLYING_SUPER_PARTNER.getOrdinal(),
                        CommunityApplyType.EXITING_APPLY_SUPER_PARTNER.getOrdinal())
                .orderByDesc(SuperPartnerApplyRecord.CREATE_TIME);

        return this.list(rw);
    }


}














