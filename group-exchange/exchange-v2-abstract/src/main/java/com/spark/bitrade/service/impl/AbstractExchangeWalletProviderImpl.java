package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.dsc.DscContext;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.job.DelayWalSyncJob;
import com.spark.bitrade.service.ExchangeRateService;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.service.ExchangeWalletProvider;
import com.spark.bitrade.service.ExchangeWalletWalRecordService;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.trans.Tuple2;
import com.spark.bitrade.uitl.WalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * ExchangeWalletProviderImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/3 16:55
 */
@Slf4j
public abstract class AbstractExchangeWalletProviderImpl implements ExchangeWalletProvider {

    protected ExchangeWalletOperations operations;
    protected ExchangeWalletWalRecordService recordService;
    protected DelayWalSyncJob delayWalSyncJob;

    protected ExchangeRateService rateService;

    protected DscContext dscContext;

    @Autowired
    public void setOperations(ExchangeWalletOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void setRecordService(ExchangeWalletWalRecordService recordService) {
        this.recordService = recordService;
    }

    @Autowired
    public void setDelayWalSyncJob(DelayWalSyncJob delayWalSyncJob) {
        this.delayWalSyncJob = delayWalSyncJob;
    }

    @Autowired
    public void setRateService(ExchangeRateService rateService) {
        this.rateService = rateService;
    }

    @Autowired
    public void setDscContext(DscContext dscContext) {
        this.dscContext = dscContext;
    }

    @Override
    public boolean freezeBalance(ExchangeOrder order) {

        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        // 关联会员和订单
        record.setMemberId(order.getMemberId());
        record.setRefId(order.getOrderId());

        // 决定扣除币种和数额
        // 购买
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            // 结算币
            record.setCoinUnit(order.getBaseSymbol());
        } else {
            // 交易币
            record.setCoinUnit(order.getCoinSymbol());
        }

        // 手续费。。。
        // record.setFee();

        // - 余额
        record.setTradeBalance(WalletUtils.negativeOf(order.getFreezeAmount()));
        // + 冻结
        record.setTradeFrozen(WalletUtils.positiveOf(order.getFreezeAmount()));

        // 状态
        record.setTradeType(WalTradeType.PLACE_ORDER);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        // record.setTccStatus(); default 0 暂未使用到

        // 备注信息
        String remark = String.format("symbol=%s,amount=%s,price=%s,type=%s,direction=%s", order.getSymbol(),
                order.getAmount(), order.getPrice(), order.getType().name(), order.getDirection().name());
        record.setRemark(remark);

        record.setCreateTime(Calendar.getInstance().getTime());

        return operations.booking(record).isPresent();
    }

    // 此方法调用时，订单成交完成或被撤单
    @Override
    public boolean giveBackFrozen(OrderSettleDelta delta, WalTradeType type) {

        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        record.setMemberId(delta.getMemberId());
        record.setRefId(delta.getOrderId());
        record.setCoinUnit(delta.getCoinSymbol());

        // + 余额
        record.setTradeBalance(WalletUtils.positiveOf(delta.getReturnAmount()));
        // - 冻结
        record.setTradeFrozen(WalletUtils.negativeOf(delta.getReturnAmount()));

        // 手续费
        // record.setFee();

        // 状态
        record.setTradeType(type);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        // record.setTccStatus(); default 0 暂未使用到

        // 备注信息
        String remark = String.format("frozenBalance=%s,dealBalance=%s,returnAmount=%s",
                delta.getFrozenBalance(), delta.getDealBalance(), delta.getReturnAmount());
        record.setRemark(remark);

        record.setCreateTime(Calendar.getInstance().getTime());

        return operations.booking(record).isPresent();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Tuple2<ExchangeWalletWalRecord, ExchangeWalletWalRecord> tradeSettle(TradeSettleDelta delta) {

        // 构建日志

        // 收入币种
        ExchangeWalletWalRecord income = builderIncomeExchangeWalletWalRecord(delta);

        // 支付币种
        ExchangeWalletWalRecord outcome = builderOutcomeExchangeWalletWalRecord(delta);

        // 不涉及到缓存更改, 直接添加同步任务请求
        delayWalSyncJob.sync(delta.getMemberId(), delta.getIncomeSymbol());
        delayWalSyncJob.sync(delta.getMemberId(), delta.getOutcomeSymbol());

        // 签名
        dscContext.getDscEntityResolver(income).update();
        dscContext.getDscEntityResolver(outcome).update();
        if (recordService.save(income) && recordService.save(outcome)) {
            //if (recordService.saveBatch(Arrays.asList(income, outcome))) {
            return new Tuple2<>(income, outcome);
        }
        throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
    }

    /**
     * 构建收入记录
     *
     * @param delta
     * @return
     */
    protected ExchangeWalletWalRecord builderIncomeExchangeWalletWalRecord(TradeSettleDelta delta) {
        ExchangeWalletWalRecord income = new ExchangeWalletWalRecord();

        income.setMemberId(delta.getMemberId());
        income.setRefId(delta.getOrderId());
        income.setCoinUnit(delta.getIncomeSymbol());

        // + 余额
        income.setTradeBalance(WalletUtils.positiveOf(delta.getIncomeCoinAmount()));
        income.setTradeFrozen(BigDecimal.ZERO);
        income.setTradeType(WalTradeType.TURNOVER);

        // 手续费
        income.setFee(delta.getRealFee());
        income.setFeeDiscount(delta.getFeeDiscount());
        income.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        income.setCreateTime(new Date());
        income.setSyncId(0L);
        // 设置撮合类型
        income.setOrderMatchType(delta.getType());

        income.setRate(rateService.gateUsdRate(delta.getIncomeSymbol()));
        income.setRemark("匹配订单：" + delta.getRefOrderId());
        return income;
    }

    /**
     * 构建支出记录
     *
     * @param delta
     * @return
     */
    protected ExchangeWalletWalRecord builderOutcomeExchangeWalletWalRecord(TradeSettleDelta delta) {
        ExchangeWalletWalRecord outcome = new ExchangeWalletWalRecord();
        outcome.setMemberId(delta.getMemberId());
        outcome.setRefId(delta.getOrderId());
        outcome.setCoinUnit(delta.getOutcomeSymbol());

        // - 冻结
        outcome.setTradeBalance(BigDecimal.ZERO);
        outcome.setTradeFrozen(WalletUtils.negativeOf(delta.getOutcomeCoinAmount()));
        outcome.setTradeType(WalTradeType.TURNOVER);
        outcome.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        outcome.setCreateTime(new Date());
        outcome.setSyncId(0L);
        // 设置撮合类型
        outcome.setOrderMatchType(delta.getType());

        outcome.setRate(rateService.gateUsdRate(delta.getOutcomeSymbol()));
        outcome.setRemark("匹配订单：" + delta.getRefOrderId());
        return outcome;
    }
}
