package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.entity.SuperMemberCommunity;
import com.spark.bitrade.entity.SuperPartnerApplyRecord;
import com.spark.bitrade.entity.SuperPartnerCommunity;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.SuperMemberCommunityMapper;
import com.spark.bitrade.mapper.SuperPartnerApplyRecordMapper;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.SuperMemberCommunityService;
import com.spark.bitrade.service.SuperPartnerCommunityService;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.CommunityMemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Service
@Slf4j
public class SuperMemberCommunityServiceImpl extends ServiceImpl<SuperMemberCommunityMapper, SuperMemberCommunity> implements SuperMemberCommunityService {

    @Autowired
    private SuperPartnerApplyRecordMapper superPartnerApplyRecordMapper;

    @Autowired
    private SuperMemberCommunityMapper superMemberCommunityMapper;

    @Autowired
    private SuperPartnerCommunityService superPartnerCommunityService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    /**
     * 查询用户加入的社区
     *
     * @param memberId
     * @return
     */
    @Override
    public SuperMemberCommunity findMemberCommunity(Long memberId) {
        QueryWrapper<SuperMemberCommunity> mw = new QueryWrapper<>();
        mw.eq(SuperMemberCommunity.MEMBER_ID, memberId)
                .eq(SuperMemberCommunity.STATUS, InCommunityStatus.IN_COMMUNITY.getOrdinal());
        List<SuperMemberCommunity> memberCommunityList = this.list(mw);
        if (!CollectionUtils.isEmpty(memberCommunityList)) {
            return memberCommunityList.get(0);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void joinCommunity(Long memberId, Long communityId) {
        log.info("=====================用户memberId:{}加入社区communityId:{} 开始=======================", memberId, communityId);
        //构建社区成员实体
        SuperMemberCommunity community = new SuperMemberCommunity();
        community.setCommunityId(communityId)
                .setCreateTime(new Date())
                .setMemberId(memberId).setStatus(InCommunityStatus.IN_COMMUNITY)
                .setUpdateTime(new Date());
        int insert = superMemberCommunityMapper.insert(community);
        log.info("=====================用户memberId:{}加入社区communityId:{} 构建社区成员实体:{}", memberId, communityId, insert);

        //社区人数增加
        SuperPartnerCommunity partnerCommunity = superPartnerCommunityService.findById(communityId, BooleanEnum.IS_TRUE);
        AssertUtil.notNull(partnerCommunity, LockMsgCode.COMMUNITY_IS_NOT_FIND);
        AssertUtil.isTrue(!memberId.equals(partnerCommunity.getMemberId()),LockMsgCode.YOU_CANT_JOIN_SELF_COMMUNITY);

        partnerCommunity.setPeopleCount(partnerCommunity.getPeopleCount() + 1);
        boolean b = superPartnerCommunityService.updateById(partnerCommunity);

        log.info("=====================用户memberId:{}加入社区communityId:{} 社区人数增加:{}", memberId, communityId, b);

        //生成成员加入社区申请记录 默认已通过
        SuperPartnerApplyRecord record = new SuperPartnerApplyRecord();
        record.setApplyType(CommunityApplyType.JOIN_COMMUNITY)
                .setAuditStatus(SuperAuditStatus.APPROVED)
                .setAuditTime(new Date())
                .setCommunityId(communityId)
                .setMemberId(memberId)
                .setRemark("加入社区")
                .setUpdateTime(new Date())
                .setCreateTime(new Date());
        int insertr = superPartnerApplyRecordMapper.insert(record);

        log.info("=====================用户memberId:{}加入社区communityId:{} 生成成员加入社区申请记录:{}", memberId, communityId, insertr);
        AssertUtil.isTrue(SqlHelper.retBool(insert) && b && SqlHelper.retBool(insertr), CommonMsgCode.ERROR);
        log.info("=====================用户memberId:{}加入社区communityId:{} 成功=======================", memberId, communityId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void exitCommunity(SuperMemberCommunity memberCommunity, boolean isReduce, String coinUnit) {
        Long communityId = memberCommunity.getCommunityId();
        Long memberId = memberCommunity.getMemberId();
        log.info("=====================用户memberId:{}退出社区communityId:{}================开始", memberId, communityId);

        //退出社区 状态改为已退出
        memberCommunity.setUpdateTime(new Date())
                .setStatus(InCommunityStatus.EXIT_COMMUNITY);
        int i = superMemberCommunityMapper.updateById(memberCommunity);
        AssertUtil.isTrue(i>0,CommonMsgCode.ERROR);

        log.info("=====================用户memberId:{}退出社区communityId:{},退出:{}================", memberId, communityId, i);
        //社区人数减少
        SuperPartnerCommunity partnerCommunity = superPartnerCommunityService.findById(communityId, BooleanEnum.IS_TRUE);
        AssertUtil.notNull(partnerCommunity, LockMsgCode.COMMUNITY_IS_NOT_FIND);
        partnerCommunity.setPeopleCount(partnerCommunity.getPeopleCount() - 1);
        boolean b = superPartnerCommunityService.updateById(partnerCommunity);
        AssertUtil.isTrue(b,CommonMsgCode.ERROR);


        log.info("=====================用户memberId:{}退出社区communityId:{},人数减少:{}================", memberId, communityId, b);
        //生成成员退出社区申请记录 默认已通过
        SuperPartnerApplyRecord record = new SuperPartnerApplyRecord();
        record.setApplyType(CommunityApplyType.EXIT_COMMUNITY)
                .setAuditStatus(SuperAuditStatus.APPROVED)
                .setAuditTime(new Date())
                .setCommunityId(communityId)
                .setMemberId(memberId)
                .setRemark("退出社区")
                .setUpdateTime(new Date())
                .setCreateTime(new Date());
        int insertr = superPartnerApplyRecordMapper.insert(record);
        AssertUtil.isTrue(insertr>0,CommonMsgCode.ERROR);

        if(isReduce){
            //减少用户余额
            WalletTradeEntity reduce = new WalletTradeEntity();
            reduce.setType(TransactionType.SUPER_EXIT_COMMUNITY);
            //当前用户
            reduce.setMemberId(memberId);
            reduce.setCoinUnit(coinUnit);
            //减少的数量
            reduce.setTradeBalance(new BigDecimal("-5"));
            //冻结 锁仓余额
            reduce.setTradeLockBalance(BigDecimal.ZERO);
            reduce.setTradeFrozenBalance(BigDecimal.ZERO);
            reduce.setComment("退出社区未满30天，支付给5SL给负责人");
            reduce.setServiceCharge(new ServiceChargeEntity());

            boolean reduceb = traceWallet(reduce);
            //增加负责人余额
            if(reduceb){
                //增加用户余额
                WalletTradeEntity addW = new WalletTradeEntity();
                addW.setType(TransactionType.SUPER_EXIT_COMMUNITY);
                //社区负责人
                addW.setMemberId(partnerCommunity.getMemberId());
                addW.setCoinUnit(coinUnit);
                //减少的数量
                addW.setTradeBalance(new BigDecimal("5"));
                //冻结 锁仓余额
                addW.setTradeLockBalance(BigDecimal.ZERO);
                addW.setTradeFrozenBalance(BigDecimal.ZERO);
                addW.setComment("成员退出社区未满30天，获得收益");
                addW.setServiceCharge(new ServiceChargeEntity());

                boolean add=traceWallet(addW);
                AssertUtil.isTrue(reduceb&&add,LockMsgCode.PAY_TO_PARTNER_SLU_FAILT);
            }
        }

        log.info("=====================用户memberId:{}退出社区communityId:{},退出记录:{}================", memberId, communityId, insertr);
    }

    @Override
    public Page<CommunityMemberVo> findCommunityMembers(Long communityId, int pageNo, int pageSize) {
        Page<CommunityMemberVo> page=new Page<>(pageNo,pageSize);
        List<CommunityMemberVo> communityMembers = superMemberCommunityMapper.findCommunityMembers(communityId,page);
        page.setRecords(communityMembers);
        return page;
    }

    /**
     * 执行交易
     * @return
     */
    @Override
    public boolean traceWallet(WalletTradeEntity tradeEntity) {
        //先减少余额 再给群主增加余额
        //处理账 成员减少余额
        boolean tccFlag = true;
        MessageRespResult<Boolean> trade = memberWalletApiService.trade(tradeEntity);
        AssertUtil.isTrue(trade.isSuccess(),CommonMsgCode.SERVICE_UNAVAILABLE);
        AssertUtil.isTrue(trade.getData(),CommonMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);

        return tccFlag;

    }


}
