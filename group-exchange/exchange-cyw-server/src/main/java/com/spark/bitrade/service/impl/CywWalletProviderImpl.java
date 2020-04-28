package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.constants.CywProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.job.DelayWalSyncJob;
import com.spark.bitrade.service.CywWalletOperations;
import com.spark.bitrade.service.CywWalletProvider;
import com.spark.bitrade.service.CywWalletWalRecordService;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.uitl.CywWalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * CywWalletProviderImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/3 16:55
 */
@Slf4j
@Service
public class CywWalletProviderImpl implements CywWalletProvider {

    private CywWalletOperations operations;
    private CywWalletWalRecordService recordService;
    private DelayWalSyncJob delayWalSyncJob;

    @Autowired
    public void setOperations(CywWalletOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void setRecordService(CywWalletWalRecordService recordService) {
        this.recordService = recordService;
    }

    @Autowired
    public void setDelayWalSyncJob(DelayWalSyncJob delayWalSyncJob) {
        this.delayWalSyncJob = delayWalSyncJob;
    }

    @Override
    public boolean freezeBalance(ExchangeCywOrder order) {

        // 构建wal
        CywWalletWalRecord record = new CywWalletWalRecord();

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
        record.setTradeBalance(CywWalletUtils.negativeOf(order.getFreezeAmount()));
        // + 冻结
        record.setTradeFrozen(CywWalletUtils.positiveOf(order.getFreezeAmount()));

        // 状态
        record.setTradeType(WalTradeType.PLACE_ORDER);
        record.setStatus(CywProcessStatus.NOT_PROCESSED);
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
        CywWalletWalRecord record = new CywWalletWalRecord();

        record.setMemberId(delta.getMemberId());
        record.setRefId(delta.getOrderId());
        record.setCoinUnit(delta.getCoinSymbol());

        // + 余额
        record.setTradeBalance(CywWalletUtils.positiveOf(delta.getReturnAmount()));
        // - 冻结
        record.setTradeFrozen(CywWalletUtils.negativeOf(delta.getReturnAmount()));

        // 手续费
        // record.setFee();

        // 状态
        record.setTradeType(type);
        record.setStatus(CywProcessStatus.NOT_PROCESSED);
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
    public boolean tradeSettle(TradeSettleDelta delta) {

        // 构建日志

        // 收入币种
        CywWalletWalRecord income = new CywWalletWalRecord();

        income.setMemberId(delta.getMemberId());
        income.setRefId(delta.getOrderId());
        income.setCoinUnit(delta.getIncomeSymbol());

        // + 余额
        income.setTradeBalance(CywWalletUtils.positiveOf(delta.getIncomeCoinAmount()));
        income.setTradeFrozen(BigDecimal.ZERO);
        income.setTradeType(WalTradeType.TURNOVER);

        // 手续费
        income.setFee(delta.getRealFee());
        income.setFeeDiscount(delta.getFeeDiscount());
        income.setStatus(CywProcessStatus.NOT_PROCESSED);
        income.setCreateTime(new Date());
        income.setRemark("匹配订单：" + delta.getRefOrderId());


        // 支付币种
        CywWalletWalRecord outcome = new CywWalletWalRecord();
        outcome.setMemberId(delta.getMemberId());
        outcome.setRefId(delta.getOrderId());
        outcome.setCoinUnit(delta.getOutcomeSymbol());

        // - 冻结
        outcome.setTradeBalance(BigDecimal.ZERO);
        outcome.setTradeFrozen(CywWalletUtils.negativeOf(delta.getOutcomeCoinAmount()));
        outcome.setTradeType(WalTradeType.TURNOVER);
        outcome.setStatus(CywProcessStatus.NOT_PROCESSED);
        outcome.setCreateTime(new Date());
        outcome.setRemark("匹配订单：" + delta.getRefOrderId());

        // 不涉及到缓存更改, 直接添加同步任务请求
        delayWalSyncJob.sync(delta.getMemberId(), delta.getIncomeSymbol());
        delayWalSyncJob.sync(delta.getMemberId(), delta.getOutcomeSymbol());

        return recordService.saveBatch(Arrays.asList(income, outcome));
    }
}
