package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.IncubatorsDetailStatus;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.entity.IncubatorsFundDetails;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.form.WarehouseUpgradeForm;
import com.spark.bitrade.mapper.IncubatorsFundDetailsMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.LockUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 孵化区-解锁仓明细表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Service
public class IncubatorsFundDetailsServiceImpl extends ServiceImpl<IncubatorsFundDetailsMapper, IncubatorsFundDetails> implements IncubatorsFundDetailsService {

    @Autowired
    private SuperMemberCommunityService superMemberCommunityService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private IncubatorsBasicInformationService incubatorsBasicInformationService;
    @Autowired
    private LockCoinDetailService lockCoinDetailService;

    /**
     * 获取升仓数量
     *
     * @param incubatorsDetailStatus 状态
     * @param incubatorsId           关联id
     * @return
     */
    @Override
    public IncubatorsFundDetails getIncubatorsFundDetailsById(IncubatorsDetailStatus incubatorsDetailStatus, Long incubatorsId) {
        QueryWrapper<IncubatorsFundDetails> wrapper = new QueryWrapper<>();
        wrapper.eq(IncubatorsFundDetails.INCUBATORS_ID, incubatorsId)
                .eq(IncubatorsFundDetails.STATUS, incubatorsDetailStatus.getOrdinal());
        List<IncubatorsFundDetails> list = getBaseMapper().selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 04升仓
     *
     * @param member 会员信息
     * @param form   提交表单
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageRespResult warehouseUpgrade(Member member, WarehouseUpgradeForm form) {
        Long memberId = member.getId();
        BigDecimal lockAmount = form.getNum();
        // 验证资金密码
        LockUtil.validateJyPassword(form.getJyPassword(), member.getJyPassword(), member.getSalt());
        // 判断钱包SLU是否满足条件
        MessageRespResult<MemberWallet> wallet = memberWalletApiService.getWallet(memberId, "SLU");
        AssertUtil.isTrue(wallet.isSuccess(), CommonMsgCode.SERVICE_UNAVAILABLE);
        MemberWallet walletData = wallet.getData();
        AssertUtil.isTrue(walletData.getBalance().compareTo(lockAmount) >= 0,
                LockMsgCode.SLU_WALLET_NOT_FIND_BLANCE_BUZU);
        Long id = form.getId();
        // 修改孵化区状态
        Integer countDetail = baseMapper.updateIncubatorsFundDetails(id, lockAmount);
        AssertUtil.isTrue(countDetail == 1, CommonMsgCode.FAILURE);
        Integer count = incubatorsBasicInformationService.updateIncubatorsBasicInformation(id, lockAmount);
        AssertUtil.isTrue(count == 1, CommonMsgCode.FAILURE);
        // 修改孵化区lock_coin_detail
        LockCoinDetail lockCoinDetail = lockCoinDetailService.getLockCoinDetailByMemberIdAndTypeAndStatus(memberId, LockType.INCUBOTORS_LOCK, LockStatus.LOCKED, id);
        AssertUtil.isTrue(lockCoinDetail != null, CommonMsgCode.FAILURE);
        BigDecimal oldLockAmount = lockCoinDetail.getTotalAmount();
        lockCoinDetail.setStatus(LockStatus.CANCLE);
        lockCoinDetail.setRemark("孵化区上币，本次升仓前总锁仓数量：" + oldLockAmount + "，待升仓数量：" + lockAmount);
        boolean result = lockCoinDetailService.updateById(lockCoinDetail);
        AssertUtil.isTrue(result, CommonMsgCode.UNCHECKED_ERROR);
        LockCoinDetail coinDetail = new LockCoinDetail();
        BigDecimal newLockAmount = lockAmount.add(oldLockAmount);
        coinDetail.setCoinUnit("SLU");
        coinDetail.setMemberId(memberId);
        coinDetail.setRemainAmount(lockCoinDetail.getRemainAmount().add(lockAmount));
        coinDetail.setStatus(LockStatus.LOCKED);
        coinDetail.setTotalAmount(newLockAmount);
        coinDetail.setType(LockType.INCUBOTORS_LOCK);
        coinDetail.setRefActivitieId(id);
        coinDetail.setLockTime(new Date());
        coinDetail.setRemark("孵化区上币，本次升仓前总锁仓数量：" + oldLockAmount + "，升仓数量：" + lockAmount);
        boolean co = lockCoinDetailService.save(coinDetail);
        AssertUtil.isTrue(co, CommonMsgCode.UNCHECKED_ERROR);
        // 调用资金接口 生成流水
        WalletTradeEntity addW = new WalletTradeEntity();
        addW.setType(TransactionType.INCUBOTORS_LOCK);
        addW.setMemberId(memberId);
        addW.setCoinUnit("SLU");
        // 减少的数量
        addW.setTradeBalance(BigDecimal.ZERO.subtract(lockAmount));
        // 冻结 锁仓余额
        addW.setTradeLockBalance(lockAmount);
        addW.setTradeFrozenBalance(BigDecimal.ZERO);
        addW.setComment("孵化区上币，升仓锁仓");
        addW.setServiceCharge(new ServiceChargeEntity());
        superMemberCommunityService.traceWallet(addW);
        return MessageRespResult.success();
    }
}
