package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.entity.SuperPartnerApplyRecord;
import com.spark.bitrade.entity.SuperPartnerCommunity;
import com.spark.bitrade.form.SuperPartnerCommunityForm;
import com.spark.bitrade.mapper.SuperPartnerApplyRecordMapper;
import com.spark.bitrade.mapper.SuperPartnerCommunityMapper;
import com.spark.bitrade.service.SuperPartnerCommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 合伙人用户关联表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Service
@Slf4j
public class SuperPartnerCommunityServiceImpl extends ServiceImpl<SuperPartnerCommunityMapper, SuperPartnerCommunity> implements SuperPartnerCommunityService {


    @Resource
    private SuperPartnerCommunityMapper superPartnerCommunityMapper;

    @Resource
    private SuperPartnerApplyRecordMapper superPartnerApplyRecordMapper;
 //   modify by qhliao 会员体系 取消锁仓
//    @Autowired
//    private SuperMemberCommunityService superMemberCommunityService;
//
//    @Autowired
//    private LockCoinDetailService lockCoinDetailService;

    /**
     * 查询根据社区id 并缓存
     *
     * @param id 社区id
     * @return
     */
    @Override
    public SuperPartnerCommunity findById(Long id, BooleanEnum usable) {
        QueryWrapper<SuperPartnerCommunity> wrapper = new QueryWrapper<>();
        wrapper.eq(SuperPartnerCommunity.ID, id)
                .eq(SuperPartnerCommunity.USABLE, usable.getOrdinal());
        return superPartnerCommunityMapper.selectOne(wrapper);
    }


    /**
     * 查询根据memberId 并缓存
     *
     * @param memberId memberId
     * @return
     */
    @Override
    public SuperPartnerCommunity findByMemberId(Long memberId, BooleanEnum usable) {
        QueryWrapper<SuperPartnerCommunity> wrapper = new QueryWrapper<>();
        wrapper.eq(SuperPartnerCommunity.MEMBER_ID, memberId)
                .eq(SuperPartnerCommunity.USABLE, usable.getOrdinal());
        List<SuperPartnerCommunity> superPartnerCommunity = superPartnerCommunityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(superPartnerCommunity)) {
            return null;
        }
        return superPartnerCommunity.get(0);
    }
    @Override
    public SuperPartnerCommunity findByMemberId(Long memberId) {
        QueryWrapper<SuperPartnerCommunity> wrapper = new QueryWrapper<>();
        wrapper.eq(SuperPartnerCommunity.MEMBER_ID, memberId).orderByDesc(SuperPartnerCommunity.CREATE_TIME);
        List<SuperPartnerCommunity> superPartnerCommunity = superPartnerCommunityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(superPartnerCommunity)) {
            return null;
        }
        return superPartnerCommunity.get(0);
    }


    /**
     * 创建合伙人即创建社区
     *
     * @param memberId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSuperPartner(Long memberId, SuperPartnerCommunityForm form) {
        //创建社区
        log.info("=============创建社区memberId:{}==============", memberId);
        SuperPartnerCommunity community = new SuperPartnerCommunity();
        community.setPeopleCount(0)
                .setCreateTime(new Date())
                .setEquityStatus(EquityStatus.NORMAL)
                .setMemberId(memberId)
                .setUpdateTime(new Date())
                .setUsable(BooleanEnum.IS_FALSE)
                .setWechatCode(form.getWechatCodeUrl())
                .setWechatNum(form.getWechatNum())
                .setCommunityName(form.getCommunityName())
                .setReferrerId(form.getReferrerId());
        this.save(community);
        log.info("=============创建社区communityId:{}==============", community.getId());

        //创建申请记录

        SuperPartnerApplyRecord record = new SuperPartnerApplyRecord();
        record.setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setRemark(form.getApplyReason())
                .setMemberId(memberId)
                .setApplyType(CommunityApplyType.APPLYING_SUPER_PARTNER)
                .setCommunityId(community.getId())
                .setAuditStatus(SuperAuditStatus.PENDING)
                .setCreateTime(new Date());
        int insert = superPartnerApplyRecordMapper.insert(record);
        log.info("=============创建社区生成申请记录recordId:{}==============", record.getId());

        //modify by qhliao 会员体系 取消锁仓
//        log.info("=============创建社区生成锁仓记录==============");
//        LockCoinDetail detail=new LockCoinDetail();
//        detail.setCoinUnit(form.getCoinUnit());
//        detail.setMemberId(memberId);
//        detail.setLockTime(new Date());
//        detail.setPlanIncome(BigDecimal.ZERO);
//        detail.setTotalAmount(form.getAmount());
//        detail.setType(LockType.SUPER_PARTNER);
//        detail.setRemark("超级合伙人锁仓");
//        //SLU相对USDT的
//        detail.setLockPrice(form.getUsdtRate());
//        detail.setTotalcny(form.getCnytRate().multiply(form.getAmount()));
//        //USDT相对 CNY的
//        detail.setUsdtPricecny(form.getCnytRate());
//        detail.setStatus(LockStatus.LOCKED);
//        detail.setRemainAmount(form.getAmount());
//        lockCoinDetailService.save(detail);
//        log.info("=============创建社区生成锁仓记录成功detailId:{}==============",detail.getId());
//        //冻结余额 锁仓
//        log.info("=============创建社区锁仓开始==============");
//
//        WalletTradeEntity addW = new WalletTradeEntity();
//        addW.setType(TransactionType.SUPER_PARTNER_LOCK);
//        //社区负责人
//        addW.setMemberId(memberId);
//        addW.setCoinUnit(form.getCoinUnit());
//        //减少的数量
//        addW.setTradeBalance(BigDecimal.ZERO.subtract(form.getAmount()));
//        //冻结 锁仓余额
//        addW.setTradeLockBalance(form.getAmount());
//        addW.setTradeFrozenBalance(BigDecimal.ZERO);
//        addW.setComment("超级合伙人锁仓");
//        addW.setServiceCharge(new ServiceChargeEntity());
//        boolean b = superMemberCommunityService.traceWallet(addW);
//        AssertUtil.isTrue(b,LockMsgCode.PAY_TO_PARTNER_SLU_FAILT);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitSuperPartner(Long memberId,SuperPartnerCommunity partnerCommunity) {

        log.info("=============退出合伙人申请创建memberId:{}=====================", memberId);
        //新增 孵化区注册的合伙人不能退出
        SuperPartnerApplyRecord record=new SuperPartnerApplyRecord();
        record.setCreateTime(new Date())
                .setAuditStatus(SuperAuditStatus.PENDING)
                .setCommunityId(partnerCommunity.getId())
                .setApplyType(CommunityApplyType.EXITING_APPLY_SUPER_PARTNER)
                .setMemberId(memberId)
                .setUpdateTime(new Date())
                .setCreateTime(new Date());
        int insert = superPartnerApplyRecordMapper.insert(record);

        log.info("=============退出合伙人申请创建recordId:{}=====================", record.getId());

    }

    /**
     * 获取是否主动创建社区
     *
     * @param memberId          会员ID
     * @param usable            是否有效
     * @param superRegistration 来源渠道
     * @return
     */
    @Override
    public SuperPartnerCommunity getSuperPartnerCommunityByMemmberId(Long memberId, BooleanEnum usable, SuperRegistration superRegistration) {
        QueryWrapper<SuperPartnerCommunity> wrapper = new QueryWrapper<>();
        wrapper.eq(SuperPartnerCommunity.MEMBER_ID, memberId)
                .eq(SuperPartnerCommunity.SOURCE_CHANNEL,superRegistration.getOrdinal())
                .eq(SuperPartnerCommunity.USABLE, usable.getOrdinal());
        List<SuperPartnerCommunity> superPartnerCommunity = superPartnerCommunityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(superPartnerCommunity)) {
            return null;
        }
        return superPartnerCommunity.get(0);
    }

}

















