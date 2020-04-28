package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.AwardTaskType;
import com.spark.bitrade.constant.ProcessStatus;
import com.spark.bitrade.constant.TradeBehaviorType;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisStringService;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.trans.Tuple2;
import com.spark.bitrade.uitl.WalletUtils;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * ExchangeWalletProviderImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/3 16:55
 */
@Slf4j
@Service
public class ExchangeWalletProviderImpl extends AbstractExchangeWalletProviderImpl {
    @Autowired
    private ExchangeReleaseTaskService releaseTaskService;
    @Autowired
    private ExchangeReleaseReferrerOrderService referrerOrderService;
    @Autowired
    private ExchangeReleaseAwardTaskService awardTaskService;
    @Autowired
    private IMemberApiService memberApiService;
    @Autowired
    private RedisStringService redisStringService;
    @Autowired
    private GlobalParamService globalParamService;
    @Autowired
    private ExchangeOrderService orderService;
    @Autowired
    private ExchangeReleaseAwardTotalService awardTotalService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tuple2<ExchangeWalletWalRecord, ExchangeWalletWalRecord> tradeSettle(TradeSettleDelta delta) {
        // 适配活动：买入后24小时到账，并释放锁仓的币

        // 收入币种
        ExchangeWalletWalRecord income = this.builderIncomeExchangeWalletWalRecord(delta);

        // 支付币种
        ExchangeWalletWalRecord outcome = this.builderOutcomeExchangeWalletWalRecord(delta);

        //　冻结记录
        ExchangeWalletWalRecord freeze = this.builderFreezeExchangeWalletWalRecord(income);

        // 签名
        dscContext.getDscEntityResolver(income).update();
        dscContext.getDscEntityResolver(outcome).update();
        dscContext.getDscEntityResolver(freeze).update();

        if (recordService.save(income)
                && recordService.save(outcome)
                && recordService.save(freeze)) {
            // 创建释放任务
            releaseTaskService.saveReleaseTask(delta, freeze);

            // 买入：返佣奖励、累计奖励
            this.doAward(delta);

            // 不涉及到缓存更改, 直接添加同步任务请求
            delayWalSyncJob.sync(delta.getMemberId(), delta.getIncomeSymbol());
            delayWalSyncJob.sync(delta.getMemberId(), delta.getOutcomeSymbol());

            return new Tuple2<>(income, outcome);
        }
        throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
    }

    /**
     * 买入：返佣奖励、累计奖励
     *
     * @param delta
     */
    public void doAward(TradeSettleDelta delta) {
        // 使用卖方的手续费
        if (delta.getType().equals(TradeBehaviorType.SELL_TAKER)
                || delta.getType().equals(TradeBehaviorType.SELL_MAKER)) {
            // 卖方对应的 为直推用户的买单
            ExchangeReleaseReferrerOrder referrerOrder = referrerOrderService.findOne(delta.getRefOrderId());
            if (Objects.isNull(referrerOrder)) {
                // 未获取到记录，则说明直推用户ESP可用余额为0，进行返佣奖励、累计奖励

                // 查询买单
                ExchangeOrder order = orderService.getById(delta.getRefOrderId());
                if (Objects.isNull(order)) {
                    log.warn("未查询到买单，订单号={}", delta.getRefOrderId());
                    return;
                }

                // 查询买方用户信息
                MessageRespResult<Member> result = memberApiService.getMember(order.getMemberId());
                if (result.isSuccess()
                        && Objects.nonNull(result.getData()) && Objects.nonNull(result.getData().getInviterId())) {
                    Long inviterId = result.getData().getInviterId();
                    long inviteeId = order.getMemberId();

                    // 1）推荐人手续费奖励任务，使用 卖方手续费，返USDT
                    this.saveAward4FeeTask(inviterId, inviteeId, delta);


                    // 2） 到推荐人 直推累计数量里
                    ExchangeReleaseAwardTotal awardTotal = awardTotalService.getById(inviterId);
                    // 2.1）交易记录是否存在
                    if (Objects.isNull(awardTotal)) {
                        // 无累计创建记录，第一次不进行累计奖励
                        ExchangeReleaseAwardTotal record = new ExchangeReleaseAwardTotal();
                        record.setMemberId(inviterId);
                        // 此处为 买单对应卖单的支付币种和数量
                        record.setSymbol(delta.getOutcomeSymbol());
                        record.setTotalBuyAmount(delta.getOutcomeCoinAmount());
                        awardTotalService.save(record);
                        return;
                    }

                    // 2.2）直推用户每累计买入5000个ESP，推荐人一次性获得40ESP奖励
                    // 计算出 最低累计买入奖励总数量
                    BigDecimal minTotalBuyAmount = BigDecimalUtil.mul2down(getAccumulationBuyTotalAmount(), new BigDecimal(awardTotal.getTotalAwardTimes() + 1));
                    boolean giveRewardFlag = false;
                    if (awardTotal.getTotalBuyAmount().subtract(minTotalBuyAmount).compareTo(BigDecimal.ZERO) > 0) {
                        // 满足累计买入奖励发放要求： 更新累计数量，已释放数量，奖励数量
                        giveRewardFlag = awardTotalService.updateAward(inviterId, delta.getOutcomeCoinAmount(), this.getAccumulationBuyAwardAmount(),
                                awardTotal.getTotalAwardTimes(), minTotalBuyAmount);
                        if (giveRewardFlag) {
                            log.info("发放累计奖励：{}", awardTotal);
                            // 发放 累计奖励任务
                            this.saveAward4AccumulationTask(inviterId, inviteeId, delta);
                        }
                    }

                    // 2.3）未发放奖励，更新累计数量
                    if (!giveRewardFlag) {
                        awardTotalService.addTotalBuyAmount(inviterId, delta.getOutcomeCoinAmount());
                    }
                } else {
                    log.info("不满足奖励要求：result={}, inviterInfo={}", result.isSuccess(), result.getData());
                }
            } else {
                log.info("获取到订单，不进行返佣奖励、累计奖励。orderId={}", delta.getOrderId());
            }
        }
    }


    @Override
    protected ExchangeWalletWalRecord builderIncomeExchangeWalletWalRecord(TradeSettleDelta delta) {
        ExchangeWalletWalRecord income = super.builderIncomeExchangeWalletWalRecord(delta);
        income.setId(IdWorker.getId());
        // 因需要冻结收入，此处仅记录流水
        ///income.setStatus(ExchangeProcessStatus.PROCESSED);

        return income;
    }

    /**
     * 构建冻结记录
     *
     * @return
     */
    protected ExchangeWalletWalRecord builderFreezeExchangeWalletWalRecord(ExchangeWalletWalRecord income) {
        ExchangeWalletWalRecord freeze = new ExchangeWalletWalRecord();
        freeze.setId(IdWorker.getId());

        freeze.setMemberId(income.getMemberId());
        freeze.setRefId(income.getRefId());
        freeze.setCoinUnit(income.getCoinUnit());

        // - 余额
        freeze.setTradeBalance(WalletUtils.negativeOf(income.getTradeBalance()));
        // + 冻结
        freeze.setTradeFrozen(WalletUtils.positiveOf(income.getTradeBalance()));
        freeze.setTradeType(WalTradeType.FREEZE);

        // 手续费
        freeze.setFee(BigDecimal.ZERO);
        freeze.setFeeDiscount(BigDecimal.ZERO);
        // 此处仅记录流水
        ///freeze.setStatus(ExchangeProcessStatus.PROCESSED);
        freeze.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        freeze.setCreateTime(new Date());
        freeze.setSyncId(0L);

        freeze.setRate(rateService.gateUsdRate(income.getCoinUnit()));
        freeze.setRemark("冻结操作：关联的账户流水ID=" + income.getId());
        return freeze;
    }

    /**
     * 币币交易-推荐人奖励任务
     *
     * @param inviterId 邀请人ID
     * @param inviteeId 被邀请人ID
     * @param delta     卖方
     * @return
     */
    private void saveAward4FeeTask(long inviterId, long inviteeId, final TradeSettleDelta delta) {
        ExchangeReleaseAwardTask task = new ExchangeReleaseAwardTask();
        task.setId(IdWorker.getId());
        task.setMemberId(inviterId);
        task.setAwardSymbol(delta.getIncomeSymbol());
        // 备注：此处订单变成了卖单ID
        task.setRefId(delta.getOrderId());
        task.setRefAmount(delta.getFee());
        task.setInviteeId(inviteeId);
        task.setAmount(BigDecimalUtil.mul2down(task.getRefAmount(), this.getFeeAwardRatio(), task.getRefAmount().scale()));
        task.setType(AwardTaskType.AWARD_FOR_FEE);
        task.setStatus(ProcessStatus.NOT_PROCESSED);
        task.setReleaseTime(this.nextReleaseTime(new Date()));
        task.setRemark("推荐人奖励任务,buyOrderId=" + delta.getRefOrderId() + ",realFee=" + delta.getRealFee());

        if (BigDecimalUtil.gt0(task.getAmount())) {
            awardTaskService.save(task);
        }
    }

    /**
     * 累计买入5000USDT奖励推荐人40USDT
     *
     * @param inviterId 邀请人ID
     * @param inviteeId 被邀请人ID
     * @param delta
     * @return
     */
    private void saveAward4AccumulationTask(long inviterId, long inviteeId, final TradeSettleDelta delta) {
        ExchangeReleaseAwardTask task = new ExchangeReleaseAwardTask();
        task.setId(IdWorker.getId());
        task.setMemberId(inviterId);
        task.setAwardSymbol(delta.getOutcomeSymbol());
        // task.setRefId();
        //task.setRefAmount();
        task.setInviteeId(inviteeId);
        task.setAmount(this.getAccumulationBuyAwardAmount());
        task.setType(AwardTaskType.AWARD_FOR_ACCUMULATION);
        task.setStatus(ProcessStatus.NOT_PROCESSED);
        task.setReleaseTime(this.nextReleaseTime(new Date()));
        task.setRemark("累计购买奖励推荐人");

        awardTaskService.save(task);
    }


    private Date nextReleaseTime(Date date) {
        // 第二天 凌晨1点开始释放
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.add(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }


    /**
     * @param coinSymbol
     * @param memberId
     * @return
     */
    private String award4AccumulationKey(String coinSymbol, long memberId) {
        return new StringBuilder("data:accumulation:").append(coinSymbol).append(":").append(memberId).toString();
    }

    private BigDecimal getAccumulationBuyTotalAmount() {
        //  累计奖：直推用户累计购买基币数量
        return globalParamService.getAccumulationBuyTotalAmount();
    }

    private BigDecimal getAccumulationBuyAwardAmount() {
        // 累计奖：累计奖励数量
        return globalParamService.getAccumulationBuyAwardAmount();
    }

    private BigDecimal getFeeAwardRatio() {
        // 手续费奖励比例
        return globalParamService.getFeeAwardRatio();
    }
}
