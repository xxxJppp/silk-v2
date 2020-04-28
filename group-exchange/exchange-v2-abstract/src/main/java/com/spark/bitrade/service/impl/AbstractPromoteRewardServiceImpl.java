package com.spark.bitrade.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.RewardPromotionSetting;
import com.spark.bitrade.entity.RewardRecord;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.mapper.RewardPromotionSettingMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 返佣服务
 *
 * @author yangch
 * @since 2019-10-31 17:41:26
 */
@Slf4j
public abstract class AbstractPromoteRewardServiceImpl
        extends ServiceImpl<RewardPromotionSettingMapper, RewardPromotionSetting> implements PromoteRewardService {

    @Autowired
    protected IMemberApiService memberApiService;
    @Autowired
    protected ICoinExchange coinExchange;
    @Autowired
    protected ExchangeWalletOperations operations;
    @Autowired
    protected IMemberWalletApiService memberWalletApiService;
    @Autowired
    protected RewardRecordService rewardRecordService;


    /**
     * 创建缓存，默认10分钟过期
     */
    protected TimedCache<String, RewardPromotionSetting> timedCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 10);

    /**
     * 缓存coinUnit和coinId
     * key=coinUnit,value=coinId
     */
    protected TimedCache<String, String> coinIdCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 10);

    /**
     * 最少使用率策略,缓存500个，默认10分钟有效
     */
    protected Cache<Long, Member> memberCache = CacheUtil.newLFUCache(500, DateUnit.MINUTE.getMillis() * 10);

    @Override
    @Async("reword")
    public void reword(ExchangeWalletWalRecord incomeWalRecord) {
        if (incomeWalRecord == null || BigDecimalUtil.lte0(incomeWalRecord.getFee())) {
            log.info("手续费为0，退出返佣。记录={}", incomeWalRecord);
        }

        RewardPromotionSetting setting = this.getRewardPromotionSetting();
        if (setting == null) {
            log.warn("币币推广奖励配置不存在");
            return;
        }

        Member member = this.getMember(incomeWalRecord.getMemberId());
        if (member == null || member.getInviterId() == null) {
            log.warn("用户或推荐人不存在, member={}", member);
            return;
        }

        if (setting.getEffectiveTime() == 0
                || (DateUtil.diffDays(member.getRegistrationTime(), new Date()) <= setting.getEffectiveTime())) {
            JSONObject jsonObject = JSONObject.parseObject(setting.getInfo());

            // 获取实时汇率
            BigDecimal rate = this.getRate(incomeWalRecord.getCoinUnit());

            // 一级推荐
            BigDecimal rewardRate1 = jsonObject.getBigDecimal("one");
            Member member1 = this.promoteReward(incomeWalRecord, member, rewardRate1, RewardRecordLevel.ONE, rate);

            // 二级推荐
            BigDecimal rewardRate2 = jsonObject.getBigDecimal("two");
            Member member2 = this.promoteReward(incomeWalRecord, member1, rewardRate2, RewardRecordLevel.TWO, rate);

            // 三级推荐
            BigDecimal rewardRate3 = jsonObject.getBigDecimal("three");
            this.promoteReward(incomeWalRecord, member2, rewardRate3, RewardRecordLevel.THREE, rate);
        } else {
            log.info("不满足返佣条件,用户注册时间={}，返佣有效天数={}", member.getRegistrationTime(), setting.getEffectiveTime());
        }
    }

    /**
     * 获取返佣配置
     *
     * @return
     */
    protected RewardPromotionSetting getRewardPromotionSetting() {
        RewardPromotionSetting setting = timedCache.get("rewardPromotionSetting");
        if (setting == null) {

            QueryWrapper<RewardPromotionSetting> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", BooleanEnum.IS_TRUE);
            queryWrapper.eq("type", PromotionRewardType.EXCHANGE_TRANSACTION);

            setting = this.baseMapper.selectOne(queryWrapper);
            timedCache.put("rewardPromotionSetting", setting);
        }

        return setting;
    }

    /**
     * 获取用户
     *
     * @param memberId 会员ID
     * @return
     */
    protected Member getMember(long memberId) {
        Member member = memberCache.get(memberId);
        if (member == null) {
            MessageRespResult<Member> respResult = memberApiService.getMember(memberId);
            if (respResult.isSuccess()) {
                member = respResult.getData();
                memberCache.put(member.getId(), member);
            }
        }

        // AssertUtil.notNull(member, CommonMsgCode.MISSING_MEMBER);
        return member;
    }

    /**
     * 获取汇率
     *
     * @param coinUnit 币种
     * @return
     */
    protected BigDecimal getRate(String coinUnit) {
        return coinExchange.getUsdExchangeRate(coinUnit).getData();
    }

    /**
     * 获取coinId
     *
     * @param coinUnit 币种单位名称
     * @return
     */
    protected String getCoinId(String coinUnit) {
        String coinId = coinIdCache.get(coinUnit);
        if (coinId == null) {
            coinId = memberWalletApiService.getCoinNameByUnit(coinUnit).getData();
            coinIdCache.put(coinUnit, coinId);
        }

        return coinId;
    }


    /**
     * 邀请者返佣
     *
     * @param incomeWalRecord 币币交易收益记录
     * @param inviteeMember   被邀请者
     * @param rewardRate      返佣汇率
     * @param level           推荐级别
     * @param rate            汇率
     * @return 邀请者
     */
    protected Member promoteReward(ExchangeWalletWalRecord incomeWalRecord, Member inviteeMember,
                                 BigDecimal rewardRate, RewardRecordLevel level, BigDecimal rate) {
        if (inviteeMember == null) {
            return null;
        }

        if (inviteeMember.getInviterId() == null) {
            log.info("{} 没有推荐用户", inviteeMember.getId());
            return null;
        }

        if (rewardRate == null || rewardRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("没有配置推荐返佣,level={}", level);
            return null;
        }

        Member promoteMember = this.getMember(inviteeMember.getInviterId());
        if (promoteMember == null) {
            log.info("没有找到{}帐号的推荐用户", inviteeMember.getId());
            return null;
        }
        if (promoteMember.getStatus() == CommonStatus.ILLEGAL) {
            log.info("{}帐号已经被禁用", promoteMember.getId());
            return null;
        } else {
            BigDecimal rewardAmount = BigDecimalUtils.mulRound(incomeWalRecord.getFee(), BigDecimalUtils.getRate(rewardRate), 8);

            // 构建 返佣记录
            RewardRecord rewardRecord = this.buidlerRewardRecord(incomeWalRecord, promoteMember, rewardAmount, level);

            // 构建 返佣资金流水记录
            ExchangeWalletWalRecord promoteWalRecord =
                    this.builderExchangeWalletWalRecord(incomeWalRecord, promoteMember, rewardAmount, level, rate, rewardRecord);

            this.getService().savePromoteReward(rewardRecord, promoteWalRecord);
        }
        return promoteMember;
    }

    /**
     * 构建返佣流水记录
     *
     * @param incomeWalRecord   交易流水记录
     * @param promoteMember     返佣用户
     * @param rewardAmount      返佣数量
     * @param rewardRecordLevel 返佣级别
     * @param rate              汇率
     * @return
     */
    protected ExchangeWalletWalRecord builderExchangeWalletWalRecord(
            ExchangeWalletWalRecord incomeWalRecord, Member promoteMember,
            BigDecimal rewardAmount, RewardRecordLevel rewardRecordLevel, BigDecimal rate, RewardRecord rewardRecord) {
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();
        record.setId(IdWorker.getId());
        record.setMemberId(promoteMember.getId());
        record.setCoinUnit(incomeWalRecord.getCoinUnit());
        record.setTradeBalance(rewardAmount);
        record.setTradeFrozen(BigDecimal.ZERO);
        record.setTradeType(WalTradeType.PROMOTION_REWARD);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        record.setFee(BigDecimal.ZERO);
        record.setFeeDiscount(BigDecimal.ZERO);
        record.setRate(rate);
        // 关联 佣金记录id
        record.setRefId(rewardRecord.getId().toString());
        record.setSyncId(0L);
        record.setRemark(new StringBuilder(rewardRecordLevel.getCnName())
                .append("推荐返佣，币币交易流水id=")
                .append(incomeWalRecord.getId())
                .toString());

        return record;
    }

    /**
     * 构建返佣记录
     *
     * @param incomeWalRecord
     * @param promoteMember
     * @param rewardAmount
     * @param rewardRecordLevel
     * @return
     */
    protected RewardRecord buidlerRewardRecord(
            ExchangeWalletWalRecord incomeWalRecord, Member promoteMember,
            BigDecimal rewardAmount, RewardRecordLevel rewardRecordLevel) {
        RewardRecord record = new RewardRecord();
        record.setId(IdWorker.getId());
        record.setAmount(rewardAmount);
        //record.setCreateTime();
        record.setRemark(PromotionRewardType.EXCHANGE_TRANSACTION.getCnName());
        record.setType(RewardRecordType.PROMOTION);
        record.setCoinId(this.getCoinId(incomeWalRecord.getCoinUnit()));
        record.setMemberId(promoteMember.getId());
        record.setLevel(rewardRecordLevel);
        record.setRefTransactionId(incomeWalRecord.getId());
        //已发放
        record.setStatus(RewardRecordStatus.TREATED);
        record.setTreatedTime(new Date());
        //未进行币种转化，所以汇率为1
        record.setExchangeRate(BigDecimal.valueOf(1L));
        record.setFromAmount(rewardAmount);
        record.setFromCoinUnit(incomeWalRecord.getCoinUnit());
        record.setFromMemberId(incomeWalRecord.getMemberId());
        record.setRewardCycle(PromotionRewardCycle.REALTIME);

        return record;
    }

    /**
     * 保存记录
     *
     * @param promoteWalRecord
     */
    @Transactional(rollbackFor = Exception.class)
    public void savePromoteReward(RewardRecord rewardRecord, ExchangeWalletWalRecord promoteWalRecord) {
        // 返佣记录
        rewardRecordService.save(rewardRecord);

        // 流水记录
        operations.booking(promoteWalRecord);
    }

    public AbstractPromoteRewardServiceImpl getService() {
        return SpringContextUtil.getBean(AbstractPromoteRewardServiceImpl.class);
    }
}