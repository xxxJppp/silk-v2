package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.entity.LockCoinActivitieProject;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.mapper.LockCoinDetailMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * (LockCoinDetail)表服务实现类
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:57:04
 */
@Slf4j
@Service("lockCoinDetailService")
public class LockCoinDetailServiceImpl extends ServiceImpl<LockCoinDetailMapper, LockCoinDetail> implements LockCoinDetailService {
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private LockCoinActivitieSettingService activitieSettingService;
    @Autowired
    private IMemberApiService memberApiService;
    @Resource
    private LockCoinActivitieSettingService lockCoinActivitieSettingService;
    @Resource
    private LockCoinActivitieProjectService lockCoinActivitieProjectService;

    /**
     * 锁仓操作
     * 注意：分布式事务 处理用户的账
     *
     * @param member                   用户信息
     * @param lockCoinActivitieSetting 活动配置信息
     * @param activityCoinLockPrice    锁仓价格
     * @param usdt2CnyPrice            usdt对cny的价格
     * @param amount                   活动参与份数
     * @param boughtAmount             活动参与数量（一般和amount是一样的，如活动币种和参与活动币种不一样时，可能就不一样）
     * @return
     */
    @Override
    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public LockCoinDetail lockCoin(Member member,
                                   LockCoinActivitieSetting lockCoinActivitieSetting,
                                   BigDecimal amount,
                                   BigDecimal boughtAmount,
                                   BigDecimal activityCoinLockPrice,
                                   BigDecimal usdt2CnyPrice, LockType lockType) {
        //生成锁仓明细
        LockCoinDetail lockCoinDetail = this.generateLockCoinDetail(member,
                lockCoinActivitieSetting, activityCoinLockPrice, usdt2CnyPrice, amount, boughtAmount, lockType);

        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.ADMIN_LOCK_ACTIVITY);
        tradeEntity.setRefId(lockCoinDetail.getId().toString());
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(lockCoinDetail.getMemberId());
        //tradeEntity.setCoinId(""); //接口已处理，此处可以不用填写
        tradeEntity.setCoinUnit(lockCoinDetail.getCoinUnit());
        //根据锁仓数量 减少可用余额
        tradeEntity.setTradeBalance(lockCoinDetail.getTotalAmount().negate());
        //冻结 锁仓余额
        tradeEntity.setTradeLockBalance(lockCoinDetail.getTotalAmount());
        tradeEntity.setTradeFrozenBalance(new BigDecimal("0"));
        tradeEntity.setComment("参与锁仓活动[参考的业务表：lock_coin_detail]");
        tradeEntity.setServiceCharge(new ServiceChargeEntity());

        //处理账户的余额、资产流水
        log.info("处理账户的余额、资产流水，交易信息={}", tradeEntity);
        MessageRespResult<Boolean> tradeResult = memberWalletApiService.trade(tradeEntity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
        /*if (!tradeResult.isSuccess()) {
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.of(tradeResult.getCode(), tradeResult.getMessage()));
        }*/

        //保存锁仓明细
        log.info("保存锁仓信息={}", lockCoinDetail);
        if (!SqlHelper.retBool(this.baseMapper.insert(lockCoinDetail))) {
            ExceptionUitl.throwsMessageCodeException(LockMsgCode.LOCK_SAVE_ERROR);
        }

        //更新活动参与数量
        log.info("更新活动参与数量,amount={}", boughtAmount);
        activitieSettingService.updateBoughtAmount(lockCoinActivitieSetting.getId(), boughtAmount);

        return lockCoinDetail;
    }

    @Override
    public Integer countValidLockCoinDetail(Long memberId, LockType lockType) {
        return this.baseMapper.countValidLockCoinDetail(memberId, lockType);
    }

    @Override
    public Integer countLockCoinDetailInDay(Long memberId, LockType lockType) {
        return this.baseMapper.countLockCoinDetailInDay(memberId, lockType);
    }

    /**
     * 根据id修改返佣状态(未返佣-》已返佣)
     *
     * @param id
     * @return boolean
     * @author zhangYanjun
     * @time 2019.06.21 17:47
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRewardStatusToCompleteById(Long id) {
        return SqlHelper.retBool(this.baseMapper.updateRewardStatusToCompleteById(id));
    }

    /**
     * 根据id修改状态
     *
     * @param id
     * @return boolean
     * @author zhangYanjun
     * @time 2019.06.21 17:47
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusTById(Long id, LockStatus oldStatus, LockStatus newStatus) {
        return SqlHelper.retBool(this.baseMapper.updateStatusById(id, oldStatus, newStatus));
    }

    @Override
    public boolean updateStatusTById(Long id, LockStatus oldStatus, LockStatus newStatus, String remark) {
        return SqlHelper.retBool(this.baseMapper.updateStatusAndRemarkById(id, oldStatus, newStatus, remark));
    }

    /**
     * 生成锁仓记录
     *
     * @param member                   用户信息
     * @param lockCoinActivitieSetting 活动配置信息
     * @param activityCoinLockPrice    锁仓价格
     * @param usdt2CnyPrice            usdt对cny的价格
     * @param amount                   活动参与份数
     * @param boughtAmount             购买数量
     * @return
     */
    private LockCoinDetail generateLockCoinDetail(Member member,
                                                  LockCoinActivitieSetting lockCoinActivitieSetting,
                                                  BigDecimal activityCoinLockPrice,
                                                  BigDecimal usdt2CnyPrice,
                                                  BigDecimal amount, BigDecimal boughtAmount, LockType lockType) {
        //购买总数 = 购买数量（理解为份数）* 每份数量
        BigDecimal totalAmount = amount.multiply(lockCoinActivitieSetting.getUnitPerAmount())
                .setScale(8, BigDecimal.ROUND_DOWN);

        //添加锁仓记录
        LockCoinDetail lockCoinDetail = new LockCoinDetail();
        lockCoinDetail.setId(IdWorker.getId());
        //lockCoinDetail.setCancleTime(new Date());
        lockCoinDetail.setCoinUnit(lockCoinActivitieSetting.getCoinSymbol());
        lockCoinDetail.setLockPrice(activityCoinLockPrice);
        lockCoinDetail.setLockTime(new Date());
        lockCoinDetail.setMemberId(member.getId());
        lockCoinDetail.setPlanUnlockTime(DateUtil.addDay(new Date(), lockCoinActivitieSetting.getLockDays()));
        lockCoinDetail.setRefActivitieId(lockCoinActivitieSetting.getId());
        lockCoinDetail.setRemainAmount(totalAmount);
        lockCoinDetail.setStatus(LockStatus.LOCKED);
        lockCoinDetail.setTotalAmount(totalAmount);
        lockCoinDetail.setType(lockType);
        //lockCoinDetail.setUnlockTime(new Date());
        lockCoinDetail.setTotalcny(totalAmount.multiply(usdt2CnyPrice).setScale(8, BigDecimal.ROUND_DOWN));
        lockCoinDetail.setUsdtPricecny(usdt2CnyPrice);
        lockCoinDetail.setLockRewardSatus(LockRewardSatus.NO_REWARD);
//        lockCoinDetail.setSmsSendStatus(SmsSendStatus.NO_SMS_SEND);
        lockCoinDetail.setBeginDays(lockCoinActivitieSetting.getBeginDays());
        lockCoinDetail.setCycleDays(lockCoinActivitieSetting.getCycleDays());
        lockCoinDetail.setCycleRatio(lockCoinActivitieSetting.getCycleRatio());
        lockCoinDetail.setLockCycle(lockCoinActivitieSetting.getLockCycle());
        lockCoinDetail.setRemark("自主参加:购买数量=" + boughtAmount);

        //计算收益
        BigDecimal planIncome = lockCoinDetail.getTotalAmount().multiply(lockCoinActivitieSetting.getEarningRate());
        lockCoinDetail.setPlanIncome(planIncome);
        return lockCoinDetail;
    }

    /**
     * 用户参加锁仓活动数据校验
     *
     * @param lockType        活动类型
     * @param id              活动配置id
     * @param amount          购买数量
     * @param jyPassword      可选，资金密码
     * @param limitCountValid 可选，当前有效的活动数量限制
     * @param limitCountInDay 可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 校验成功返回活动信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @Override
    public LockCoinActivitieSetting lockVerify(Member member,
                                               LockType lockType,
                                               Long id,
                                               BigDecimal amount,
                                               BigDecimal payAmount,
                                               String jyPassword,
                                               Integer limitCountValid,
                                               Integer limitCountInDay) {
        //验证资金密码
        if (StringUtils.hasText(jyPassword)) {
            AssertUtil.hasText(jyPassword, CommonMsgCode.MISSING_JYPASSWORD);
            String mbPassword = member.getJyPassword();
            AssertUtil.hasText(mbPassword, CommonMsgCode.NO_SET_JYPASSWORD);

            MessageRespResult<Boolean> confirmResult = this.memberApiService.confirmPassword(mbPassword, jyPassword, member.getSalt());
            AssertUtil.isTrue(confirmResult.isSuccess(), CommonMsgCode.of(confirmResult.getCode(), confirmResult.getMessage()));
            AssertUtil.isTrue(confirmResult.getData(), CommonMsgCode.ERROR_JYPASSWORD);
        }

        //验证活动配置和锁仓配置是否存在
        LockCoinActivitieSetting lockCoinActivitieSetting = lockCoinActivitieSettingService.findOneByTime(id);
        AssertUtil.notNull(lockCoinActivitieSetting, LockMsgCode.NOT_HAVE_SET);
        //判断活动是否有效
        AssertUtil.isTrue(lockCoinActivitieSetting.getStatus() == LockSettingStatus.VALID,
                LockMsgCode.INVALID_ACTIVITY);
        //首先验证购买金额是否达到最低条件
        AssertUtil.isTrue(amount.compareTo(lockCoinActivitieSetting.getMinBuyAmount()) >= 0, LockMsgCode.LIMIT_MIN_BUY_AMOUNT);
        AssertUtil.isTrue(amount.compareTo(lockCoinActivitieSetting.getMaxBuyAmount()) <= 0, LockMsgCode.LIMIT_MAX_BUY_AMOUNT);
        //验证购买数量与已参与购买数量之和，是否大于最大计划总量
        BigDecimal maxPlanAmount = payAmount.add(lockCoinActivitieSetting.getBoughtAmount());
        AssertUtil.isTrue(maxPlanAmount.compareTo(lockCoinActivitieSetting.getPlanAmount()) < 1, LockMsgCode.LIMIT_OVER_PLAN_AMOUNT);


        //验证活动的限制
        LockCoinActivitieProject lockCoinActivitieProject = lockCoinActivitieProjectService.findOne(lockCoinActivitieSetting.getActivitieId());
        AssertUtil.notNull(lockCoinActivitieProject, LockMsgCode.NOT_HAVE_ACTIVITY);

        //当前有效的活动数量限制
        if (limitCountValid != null && limitCountValid > 0) {
            AssertUtil.isTrue(
                    this.countValidLockCoinDetail(member.getId(), lockType) < limitCountValid,
                    LockMsgCode.LIMIT_OVER_COUNT_VALID_CAMPAIGN);
        }

        //当日参与活动的次数限制（不考虑是否有效）
        if (limitCountInDay != null && limitCountInDay > 0) {
            AssertUtil.isTrue(
                    this.countLockCoinDetailInDay(member.getId(), lockType) < limitCountInDay,
                    LockMsgCode.LIMIT_OVER_COUNT_IN_DAY);
        }


        //验证是否超出了活动方案的总数
        BigDecimal maxProjectPlanAmount = payAmount.add(lockCoinActivitieSettingService.totalBoughtAmount(lockCoinActivitieProject.getId()));
        AssertUtil.isTrue(maxProjectPlanAmount.compareTo(lockCoinActivitieProject.getPlanAmount()) < 1, LockMsgCode.LIMIT_OVER_PROJECT_PLAN_AMOUNT);

        return lockCoinActivitieSetting;
    }

    @Override
    public List<LockCoinDetail> list(Long memberId, LockType lockType, LockStatus lockStatus) {
        return this.baseMapper.selectList(new QueryWrapper<LockCoinDetail>()
                .eq("member_id", memberId)
                .eq("type", lockType)
                .eq("status", lockStatus));
    }

    /**
     * 获取单条锁仓记录
     *
     * @param memberId       会员ID
     * @param lockType       锁仓类型
     * @param lockStatus     锁仓状态
     * @param refActivitieId 关联锁仓活动ID
     * @return
     */
    @Override
    public LockCoinDetail getLockCoinDetailByMemberIdAndTypeAndStatus(Long memberId, LockType lockType, LockStatus lockStatus, Long refActivitieId) {
        return this.baseMapper.selectOne(new QueryWrapper<LockCoinDetail>()
                .eq("member_id", memberId)
                .eq("type", lockType)
                .eq("status", lockStatus)
                .eq("ref_activitie_id", refActivitieId));
    }
    
    
    
    
    private LockCoinDetail generateLockCoinDetail(Member member,LockType lockType, BigDecimal amount,String unit,int lockDay) {
		LockCoinDetail lockCoinDetail = new LockCoinDetail();
		lockCoinDetail.setId(IdWorker.getId());
		lockCoinDetail.setCoinUnit(unit);
		lockCoinDetail.setLockPrice(new BigDecimal(0));
		lockCoinDetail.setLockTime(new Date());
		lockCoinDetail.setMemberId(member.getId());
		lockCoinDetail.setPlanUnlockTime(DateUtil.addDay(new Date(), lockDay));
		lockCoinDetail.setRemainAmount(amount);
		lockCoinDetail.setStatus(LockStatus.LOCKED);
		lockCoinDetail.setTotalAmount(amount);
		lockCoinDetail.setType(lockType);
		lockCoinDetail.setRemark(lockType.getCnName());
		
		return lockCoinDetail;
	}

    
    
    
    @Transactional(rollbackFor = Exception.class)
    public LockCoinDetail simplelock(Member member,LockType lockType,TransactionType txsType, BigDecimal amount,String unit,int lockDay, Long lockId, Long operType) {
        LockCoinDetail lockCoinDetail = null;
        if (operType == 0) {
            // 续费操作
            lockCoinDetail = this.getById(lockId);
            lockCoinDetail.setPlanUnlockTime(DateUtil.addDay(lockCoinDetail.getPlanUnlockTime(), lockDay));
        } else if (operType == 1) {
            lockCoinDetail = this.getById(lockId);
            lockCoinDetail.setRemainAmount(lockCoinDetail.getRemainAmount().add(amount));
            lockCoinDetail.setTotalAmount(lockCoinDetail.getTotalAmount().add(amount));
        } else {
            lockCoinDetail = this.generateLockCoinDetail(member,lockType, amount, unit, lockDay);
        }
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(txsType);
        tradeEntity.setRefId(lockCoinDetail.getId().toString());
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(lockCoinDetail.getMemberId());
        tradeEntity.setCoinUnit(lockCoinDetail.getCoinUnit());
        tradeEntity.setTradeBalance(lockCoinDetail.getTotalAmount().negate());
        tradeEntity.setTradeLockBalance(lockCoinDetail.getTotalAmount());
        tradeEntity.setTradeFrozenBalance(new BigDecimal(0));
        tradeEntity.setComment(lockType.getCnName());
        tradeEntity.setServiceCharge(new ServiceChargeEntity());

        log.info("处理账户的余额、资产流水，交易信息={}", tradeEntity);
        MessageRespResult<Boolean> tradeResult = memberWalletApiService.trade(tradeEntity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);

        log.info("保存锁仓信息={}", lockCoinDetail);
        if (!this.saveOrUpdate(lockCoinDetail)) {
            ExceptionUitl.throwsMessageCodeException(LockMsgCode.LOCK_SAVE_ERROR);
        }
        return lockCoinDetail;
    }

}