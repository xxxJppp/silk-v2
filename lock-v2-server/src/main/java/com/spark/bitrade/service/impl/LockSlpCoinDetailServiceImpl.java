package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  SLP模式活动锁仓接口实现
 *
 * @author young
 * @time 2019.07.12 15:50
 */
@Slf4j
@Service
public class LockSlpCoinDetailServiceImpl implements LockSlpCoinDetailService {
    @Autowired
    private LockCoinDetailService lockCoinDetailService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private LockCoinActivitieSettingService activitieSettingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LockCoinDetail lockSlpCoin(Member member,
                                      LockCoinActivitieSetting lockCoinActivitieSetting,
                                      BigDecimal amount, String payCoinUnit,
                                      BigDecimal payCoinUnitUsdtPrice,
                                      BigDecimal usdt2CnyPrice,
                                      String jyPassword,
                                      Integer limitCountValid, Integer limitCountInDay) {
        //购买总数（USDT） = 购买数量（理解为份数）* 每份数量
        BigDecimal totalAmount = amount.multiply(lockCoinActivitieSetting.getUnitPerAmount())
                .setScale(8, BigDecimal.ROUND_DOWN);
        log.info("锁仓的USDT数量：{}", totalAmount);

        //根据汇率计算支付币种的数量 = 锁仓数量（USDT）/SLP的usdt汇率
        BigDecimal payAmount = BigDecimalUtil.div2up(totalAmount, payCoinUnitUsdtPrice, 8);
        log.info("支付币种的数量：{}", payAmount);

        //锁仓数据校验
        lockCoinDetailService.lockVerify(member, LockType.LOCK_SLP, lockCoinActivitieSetting.getId(),
                amount, payAmount, jyPassword, limitCountValid, limitCountInDay);

        //生成锁仓明细
        LockCoinDetail lockCoinDetail = this.generateSlpLockCoinDetail(member,
                lockCoinActivitieSetting, payCoinUnitUsdtPrice, usdt2CnyPrice, totalAmount, payAmount, null);

        //处理账
        WalletTradeEntity tradeEntity = buildWalletTradeEntity(member,
                payCoinUnit, payAmount, lockCoinDetail,
                "参与锁仓活动[参考的业务表：lock_coin_detail]");

        //代码上通过 tcc方式保障 分布式事务 （暂未优化）
        boolean tccFlag = false;
        Long walletChangeRecordId = null;
        try {
            log.info("---------tcc处理开始-------------");
            //预处理 账户的余额、资产流水
            walletChangeRecordId = this.tradeTccTry(tradeEntity);

            //保存 slp相同价值的 锁仓记录明细
            this.saveLockCoinDetail(lockCoinDetail);

            //更新活动参与数量
            log.info("更新活动参与数量,payAmount={}", payAmount);
            activitieSettingService.updateBoughtAmount(lockCoinActivitieSetting.getId(), payAmount);

            //确认业务
            this.tradeTccConfirm(member, walletChangeRecordId);

            tccFlag = true;
            log.info("---------tcc处理结束-------------");
            return lockCoinDetail;
        } catch (MessageCodeException ex) {
            log.error("操作失败！", ex);
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.of(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            log.error("操作失败！", ex);
            ExceptionUitl.throwsMessageCodeException(LockMsgCode.LOCK_SAVE_ERROR);
        } finally {
            if (!tccFlag && walletChangeRecordId != null) {
                //取消业务
                tradeTccCancel(member, walletChangeRecordId);
            }
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LockCoinDetail upgradeSlpPackage(Member member,
                                            LockCoinActivitieSetting lockCoinActivitieSetting,
                                            BigDecimal amount, String payCoinUnit,
                                            BigDecimal payCoinUnitUsdtPrice,
                                            BigDecimal usdt2CnyPrice,
                                            String jyPassword,
                                            LockCoinDetail existLockCoinDetail) {
        //购买总数（USDT） = 购买数量（理解为份数）* 每份数量
        BigDecimal totalAmount = amount.multiply(lockCoinActivitieSetting.getUnitPerAmount())
                .setScale(8, BigDecimal.ROUND_DOWN);
        log.info("升仓套餐的USDT总数量：{}, 升仓前的USDT数量：{}", totalAmount, existLockCoinDetail.getTotalAmount());
        BigDecimal differenceTotalAmount = totalAmount.subtract(existLockCoinDetail.getTotalAmount());
        log.info("本次升仓的USDT数量：{}", differenceTotalAmount);
        AssertUtil.isTrue(BigDecimalUtil.gt0(differenceTotalAmount), LockMsgCode.INVALID_UPGRADE_PACKAGE);

        //根据汇率计算升仓支付币种的数量（需要减去旧套餐的数量） = 锁仓数量（USDT）/SLP的usdt汇率
        BigDecimal payAmount = BigDecimalUtil.div2up(differenceTotalAmount, payCoinUnitUsdtPrice, 8);
        log.info("升仓支付币种的数量：{}", payAmount);

        //锁仓数据校验
        lockCoinDetailService.lockVerify(member, LockType.LOCK_SLP, lockCoinActivitieSetting.getId(),
                amount, payAmount, jyPassword, 0, 0);

        //生成锁仓明细
        String remark = new StringBuilder("SLP升仓:支付SLP数量=").append(payAmount)
                .append("，升仓前的活动ID=").append(existLockCoinDetail.getId())
                .append("，升仓补交的USDT数量=").append(differenceTotalAmount).toString();
        LockCoinDetail lockCoinDetail = this.generateSlpLockCoinDetail(member,
                lockCoinActivitieSetting, payCoinUnitUsdtPrice, usdt2CnyPrice, totalAmount, payAmount, remark);

        //处理账
        WalletTradeEntity tradeEntity = this.buildWalletTradeEntity(member,
                payCoinUnit, payAmount, lockCoinDetail, "参与slp升仓活动[参考的业务表：lock_coin_detail]");

        //代码上通过 tcc方式保障 分布式事务 （暂未优化）
        boolean tccFlag = false;
        Long walletChangeRecordId = null;
        try {
            log.info("---------tcc处理开始-------------");
            //预处理 账户的余额、资产流水
            walletChangeRecordId = this.tradeTccTry(tradeEntity);

            log.info("取消升仓前的记录,记录={}", existLockCoinDetail);
            if (!lockCoinDetailService.updateStatusTById(existLockCoinDetail.getId(),
                    LockStatus.LOCKED, LockStatus.CANCLE,
                    new StringBuilder(existLockCoinDetail.getRemark()).append(",升仓的锁仓记录id=")
                            .append(lockCoinDetail.getId()).toString())) {
                ExceptionUitl.throwsMessageCodeException(LockMsgCode.UPGRADE_PACKAGE_SAVE_ERROR);
            }

            //保存 slp相同价值的 锁仓记录明细
            saveLockCoinDetail(lockCoinDetail);

            //更新活动参与数量
            log.info("更新活动参与数量,payAmount={}", payAmount);
            activitieSettingService.updateBoughtAmount(lockCoinActivitieSetting.getId(), payAmount);

            //确认业务
            this.tradeTccConfirm(member, walletChangeRecordId);

            tccFlag = true;
            log.info("---------tcc处理结束-------------");
            return lockCoinDetail;
        } catch (MessageCodeException ex) {
            log.error("操作失败！", ex);
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.of(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            log.error("操作失败！", ex);
            ExceptionUitl.throwsMessageCodeException(LockMsgCode.LOCK_SAVE_ERROR);
        } finally {
            if (!tccFlag && walletChangeRecordId != null) {
                //取消业务
                tradeTccCancel(member, walletChangeRecordId);
            }
        }

        return null;
    }

    /**
     * 生成锁仓记录
     *
     * @param member                   用户信息
     * @param lockCoinActivitieSetting 活动配置信息
     * @param payCoinUnitUsdtPrice     SLP兑换价格
     * @param usdt2CnyPrice            usdt对cny的价格
     * @param totalAmount              活动参与数量
     * @param payAmount                购买数量
     * @return
     */
    private LockCoinDetail generateSlpLockCoinDetail(Member member,
                                                     LockCoinActivitieSetting lockCoinActivitieSetting,
                                                     BigDecimal payCoinUnitUsdtPrice,
                                                     BigDecimal usdt2CnyPrice,
                                                     BigDecimal totalAmount, BigDecimal payAmount, String remark) {
        //添加锁仓记录
        LockCoinDetail lockCoinDetail = new LockCoinDetail();
        lockCoinDetail.setId(IdWorker.getId());
        lockCoinDetail.setCoinUnit(lockCoinActivitieSetting.getCoinSymbol());
        //SLP兑换价格
        lockCoinDetail.setLockPrice(payCoinUnitUsdtPrice);
        lockCoinDetail.setMemberId(member.getId());
        lockCoinDetail.setLockTime(new Date());
        lockCoinDetail.setRefActivitieId(lockCoinActivitieSetting.getId());
        lockCoinDetail.setPlanUnlockTime(DateUtil.addDay(new Date(), lockCoinActivitieSetting.getCycleDays()));
        //SLP不从参考此处的数量，因此默认为0
        lockCoinDetail.setRemainAmount(BigDecimal.ZERO);
        lockCoinDetail.setStatus(LockStatus.LOCKED);
        //参与SLP活动的USDT数量
        lockCoinDetail.setTotalAmount(totalAmount);
        //lockCoinDetail.setUnlockTime(new Date());
        lockCoinDetail.setType(LockType.LOCK_SLP);
        lockCoinDetail.setTotalcny(totalAmount.multiply(usdt2CnyPrice).setScale(8, BigDecimal.ROUND_DOWN));
        lockCoinDetail.setUsdtPricecny(usdt2CnyPrice);
        lockCoinDetail.setSmsSendStatus(SmsSendStatus.NO_SMS_SEND);
        lockCoinDetail.setLockRewardSatus(LockRewardSatus.NO_REWARD);
        lockCoinDetail.setBeginDays(lockCoinActivitieSetting.getBeginDays());
        lockCoinDetail.setCycleDays(lockCoinActivitieSetting.getCycleDays());
        lockCoinDetail.setCycleRatio(lockCoinActivitieSetting.getCycleRatio());
        lockCoinDetail.setLockCycle(lockCoinActivitieSetting.getLockCycle());
        if (StringUtils.hasText(remark)) {
            lockCoinDetail.setRemark(remark);
        } else {
            lockCoinDetail.setRemark("SLP锁仓:支付SLP数量=" + payAmount);
        }

        //不 计算收益
        /*BigDecimal planIncome = lockCoinDetail.getTotalAmount().multiply(lockCoinActivitieSetting.getEarningRate());
        lockCoinDetail.setPlanIncome(planIncome);*/
        lockCoinDetail.setPlanIncome(BigDecimal.ZERO);
        return lockCoinDetail;
    }

    /**
     * 构建SLP锁仓、升仓的交易实体
     *
     * @param member
     * @param payCoinUnit
     * @param payAmount
     * @param lockCoinDetail
     * @param tradeCommont
     * @return
     */
    private WalletTradeEntity buildWalletTradeEntity(Member member, String payCoinUnit, BigDecimal payAmount, LockCoinDetail lockCoinDetail, String tradeCommont) {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.SLP_LOCK);
        tradeEntity.setMemberId(member.getId());
        tradeEntity.setCoinUnit(payCoinUnit);
        tradeEntity.setRefId(lockCoinDetail.getId().toString());
        //根据锁仓数量 减少 对应的 SLP可用余额的 支付数量
        tradeEntity.setTradeBalance(payAmount.negate());
        //冻结 锁仓余额
        tradeEntity.setTradeLockBalance(BigDecimal.ZERO);
        tradeEntity.setTradeFrozenBalance(BigDecimal.ZERO);
        tradeEntity.setComment(tradeCommont);
        tradeEntity.setServiceCharge(new ServiceChargeEntity());
        return tradeEntity;
    }

    /**
     * 预处理 账户的余额、资产流水
     *
     * @param tradeEntity
     * @return 返回预处理流水记录ID
     */
    private Long tradeTccTry(WalletTradeEntity tradeEntity) {
        log.info("处理账户的余额、资产流水，交易信息={}", tradeEntity);
        MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(tradeEntity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
        AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);
        log.info("流水记录={}", tradeResult.getData());
        return tradeResult.getData().getId();
    }

    /**
     * 确认业务
     *
     * @param member
     * @param walletChangeRecordId
     */
    private void tradeTccConfirm(Member member, Long walletChangeRecordId) {
        MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(member.getId(), walletChangeRecordId);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
        AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);
    }

    /**
     * 取消业务
     *
     * @param member
     * @param walletChangeRecordId
     */
    private void tradeTccCancel(Member member, Long walletChangeRecordId) {
        try {
            log.info("取消业务-开始--用户ID={},资产变更流水ID={}", member.getId(), walletChangeRecordId);
            MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(member.getId(), walletChangeRecordId);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
            AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
            log.info("取消业务-结束--用户ID={},资产变更流水ID={}", member.getId(), walletChangeRecordId);
        } catch (Exception ex) {
            log.warn("业务取消失败，用户ID={},资产变更流水ID={}", member.getId(), walletChangeRecordId);
            log.error("业务取消失败", ex);
        }
    }

    /**
     * 保存锁仓记录
     *
     * @param lockCoinDetail
     */
    private void saveLockCoinDetail(LockCoinDetail lockCoinDetail) {
        log.info("保存锁仓信息={}", lockCoinDetail);
        if (!lockCoinDetailService.save(lockCoinDetail)) {
            ExceptionUitl.throwsMessageCodeException(LockMsgCode.LOCK_SAVE_ERROR);
        }
    }

}
