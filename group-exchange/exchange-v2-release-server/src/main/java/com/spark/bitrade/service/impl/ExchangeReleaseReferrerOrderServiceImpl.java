package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeReleaseConstants;
import com.spark.bitrade.constant.ExchangeReleaseMsgCode;
import com.spark.bitrade.constant.ReferrerOrderStatus;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeReleaseReferrerOrder;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.ExchangeReleaseReferrerOrderMapper;
import com.spark.bitrade.service.ExchangeReleaseReferrerOrderService;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.service.GlobalParamService;
import com.spark.bitrade.service.PushMessage;
import com.spark.bitrade.uitl.WalletUtils;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.SpringContextUtil;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * 币币交易-推荐人闪兑订单表(ExchangeReleaseReferrerOrder)表服务实现类
 *
 * @author yangch
 * @since 2020-01-17 17:18:13
 */
@Slf4j
@Service("exchangeReleaseReferrerOrderService")
public class ExchangeReleaseReferrerOrderServiceImpl extends ServiceImpl<ExchangeReleaseReferrerOrderMapper, ExchangeReleaseReferrerOrder> implements ExchangeReleaseReferrerOrderService {
    private ExchangeWalletOperations walletOperations;
    @Autowired
    private PushMessage pushMessage;
    @Autowired
    private GlobalParamService globalParamService;

    @Override
    public ExchangeReleaseReferrerOrder findOne(String id) {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        ExchangeReleaseReferrerOrder order = this.baseMapper.selectById(id);
        hintManager.close();
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void preExchange(ExchangeOrder order) {
        /*1、根据已成交订单信息，更新“币币交易-推荐人闪兑订单表”数量
        2、状态更改为“待完成”
        3、发送闪兑任务消息*/
        if (!order.getDirection().equals(ExchangeOrderDirection.BUY)) {
            log.info("闪兑 >>> 不处理卖单 , orderId={}", order.getOrderId());
            return;
        }

        log.info("闪兑 >>> 开始处理闪兑任务 , orderId={}", order.getOrderId());

        ExchangeReleaseReferrerOrder referrerOrder = this.findOne(order.getOrderId());
        if (Objects.nonNull(referrerOrder)) {
            if (BigDecimalUtil.gt0(order.getTradedAmount())) {
                // 计算成交均价
                referrerOrder.setRate(BigDecimalUtil.div2down(order.getTurnover(), order.getTradedAmount()));

                // 计算闪兑交易数量
                if (order.getAmount().compareTo(order.getTradedAmount()) == 0) {
                    referrerOrder.setTradedAmount(referrerOrder.getFreezeAmount());
                } else {
                    // 按成交比例进行闪兑 = referrerOrder.getFreezeAmount() * ( order.getTradedAmount()  / order.getAmount() )
                    referrerOrder.setTradedAmount(BigDecimalUtil.mul2down(referrerOrder.getFreezeAmount(),
                            BigDecimalUtil.div2down(order.getTradedAmount(), order.getAmount()),
                            referrerOrder.getFreezeAmount().scale()));
                }
                // 计算闪兑成交额
                referrerOrder.setTradedTurnover(BigDecimalUtil.mul2down(referrerOrder.getTradedAmount(), referrerOrder.getRate(),
                        referrerOrder.getTradedTurnover().scale()));

                // 计算手续费
                referrerOrder.setInviterFee(BigDecimalUtil.mul2up(referrerOrder.getTradedTurnover(), this.getExchangeRate(),
                        referrerOrder.getTradedTurnover().scale()));
                referrerOrder.setInviteeFee(BigDecimalUtil.mul2up(referrerOrder.getTradedAmount(), this.getExchangeRate(),
                        referrerOrder.getTradedAmount().scale()));
            }
            referrerOrder.setStatus(ReferrerOrderStatus.WAIT_FOR_EXCHANGE);

            // 更新闪兑订单信息
            this.baseMapper.updateById(referrerOrder);

            // 发送闪兑任务
            pushMessage.push(ExchangeReleaseConstants.TOPIC_AWARD_TASK, null, referrerOrder.getRefOrderId());
            log.info("闪兑 >>> 发送闪兑任务，orderId={}", order.getOrderId());
        } else {
            log.info("闪兑 >>> 无闪兑订单，orderId={}", order.getOrderId());
        }
    }

    @Override
    @Retryable(value = MessageCodeException.class)
    public void exchange(String orderId) {
        ExchangeReleaseReferrerOrder order = getById(orderId);
        if (Objects.isNull(order)) {
            log.warn("推荐人闪兑订单 [ ref_order_id = '{}' ] 不存在", orderId);
            ExceptionUitl.throwsMessageCodeException(ExchangeReleaseMsgCode.INEXISTENCE_ORDER);
        }

        if (order.getStatus().equals(ReferrerOrderStatus.COMPLETED)) {
            return;
        }

        if (order.getStatus().equals(ReferrerOrderStatus.TRADING)) {
            log.warn("推荐人闪兑订单 [ ref_order_id = '{}' ] 状态不匹配", orderId);
            ExceptionUitl.throwsMessageCodeException(ExchangeReleaseMsgCode.FAILED_MATCH_STATUS);
        }

        // 调用任务
        this.getService().exchange(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void exchange(ExchangeReleaseReferrerOrder order) {
        // 预处理状态到待完成，切换到主库
        if (!updateOrderState(order.getRefOrderId(), ReferrerOrderStatus.WAIT_FOR_EXCHANGE, ReferrerOrderStatus.COMPLETED)) {
            log.error("推荐人闪兑订单 [ ref_order_id = '{}' ] 状态不匹配，中止兑换流程", order.getRefOrderId());
            return;
        }

        // STEP 1
        // 推荐人 减少冻结 ESP
        if (BigDecimalUtil.gt0(order.getTradedAmount())) {
            ExchangeWalletWalRecord inviterWal = buildInviterUnfrozenWal(order);
            walletOperations.booking(inviterWal);
        }

        // 被推荐人 减少冻结 USDT
        if (BigDecimalUtil.gt0(order.getTradedTurnover())) {
            ExchangeWalletWalRecord inviteeWal = buildInviteeUnfrozenWal(order);
            walletOperations.booking(inviteeWal);
        }

        // STEP 2
        // 推荐人 增加可用 USDT
        if (BigDecimalUtil.gt0(order.getTradedTurnover())) {
            ExchangeWalletWalRecord inviterIncrementWal = buildInviterIncrementWal(order);
            walletOperations.booking(inviterIncrementWal);
        }

        // 被推荐人 增加可用 ESP
        if (BigDecimalUtil.gt0(order.getTradedAmount())) {
            ExchangeWalletWalRecord inviteeIncrementWal = buildInviteeIncrementWal(order);
            walletOperations.booking(inviteeIncrementWal);
        }

        // STEP 3
        // 退回手续费
        Optional<ExchangeWalletWalRecord> inviterGB = buildGivebackWal(order, true);
        Optional<ExchangeWalletWalRecord> inviteeGB = buildGivebackWal(order, false);

        inviterGB.ifPresent(record -> walletOperations.booking(record));
        inviteeGB.ifPresent(record -> walletOperations.booking(record));

    }

    private boolean updateOrderState(String orderId, ReferrerOrderStatus source, ReferrerOrderStatus target) {
        UpdateWrapper<ExchangeReleaseReferrerOrder> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .eq(ExchangeReleaseReferrerOrder::getRefOrderId, orderId)
                .eq(ExchangeReleaseReferrerOrder::getStatus, source)
                .set(ExchangeReleaseReferrerOrder::getStatus, target)
                .set(ExchangeReleaseReferrerOrder::getUpdateTime, new Date());

//        wrapper.eq("ref_order_id", orderId).eq("status", source)
//                .set("status", target).set("update_time", new Date());

        return update(wrapper);
    }

    // 减少推荐人ESP冻结
    private ExchangeWalletWalRecord buildInviterUnfrozenWal(ExchangeReleaseReferrerOrder order) {
        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();
        record.setMemberId(order.getInviterId());
        record.setRefId(order.getRefOrderId());

        // 推荐人，交易币，计划闪兑
        record.setCoinUnit(order.getCoinSymbol());
        // 减少实际闪兑的数量
        record.setTradeFrozen(order.getTradedAmount().negate());
        record.setTradeBalance(BigDecimal.ZERO);
        record.setFee(BigDecimal.ZERO);

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_FAST);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);

//        String remark = String.format("推荐人闪兑 refOrderId=%s,coinUnit=%s,freezeAmount=%s",
//                order.getRefOrderId(), order.getCoinSymbol(), order.getFreezeAmount());
        record.setRemark("推荐人闪兑扣除冻结");

        record.setCreateTime(Calendar.getInstance().getTime());
        return record;
    }

    // 减少被推荐人USDT冻结
    private ExchangeWalletWalRecord buildInviteeUnfrozenWal(ExchangeReleaseReferrerOrder order) {
        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();
        record.setMemberId(order.getInviteeId());
        record.setRefId(order.getRefOrderId());

        // 被推荐人，结算币，冻结数量
        record.setCoinUnit(order.getBaseSymbol());
        record.setTradeFrozen(order.getTradedTurnover().negate());
        record.setTradeBalance(BigDecimal.ZERO);
        record.setFee(BigDecimal.ZERO);

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_FAST);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);

//        String remark = String.format("被推荐人闪兑 refOrderId=%s,coinUnit=%s,freezeAmount=%s",
//                order.getRefOrderId(), order.getBaseSymbol(), order.getInviteeFreezeAmount());
        record.setRemark("被推荐人闪兑扣除冻结");

        record.setCreateTime(Calendar.getInstance().getTime());
        return record;
    }

    // 增加推荐人USDT可用
    private ExchangeWalletWalRecord buildInviterIncrementWal(ExchangeReleaseReferrerOrder order) {
        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();
        record.setMemberId(order.getInviterId());
        record.setRefId(order.getRefOrderId());

        // 推荐人，结算币，增加数量
        record.setCoinUnit(order.getBaseSymbol());
        record.setTradeFrozen(BigDecimal.ZERO);

        // 计算增加数量和手续费
        BigDecimal turnover = order.getTradedTurnover().subtract(order.getInviterFee());
        record.setTradeBalance(turnover);
        record.setFee(order.getInviterFee());

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_FAST);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);

//        String remark = String.format("推荐人闪兑 refOrderId=%s,coinUnit=%s,tradedAmount=%s,tradedTurnover=%s,fee=%s",
//                order.getRefOrderId(), order.getBaseSymbol(), order.getTradedAmount(), order.getTradedTurnover(), order.getInviterFee());
        record.setRemark("推荐人闪兑成功");

        record.setCreateTime(Calendar.getInstance().getTime());
        return record;
    }

    // 增加被推荐人ESP可用
    private ExchangeWalletWalRecord buildInviteeIncrementWal(ExchangeReleaseReferrerOrder order) {
        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();
        record.setMemberId(order.getInviteeId());
        record.setRefId(order.getRefOrderId());

        // 被推荐人，交易币，增加数量
        record.setCoinUnit(order.getCoinSymbol());
        record.setTradeFrozen(BigDecimal.ZERO);

        // 计算增加数量和手续费
        BigDecimal amount = order.getTradedAmount().subtract(order.getInviteeFee());
        record.setTradeBalance(amount);
        record.setFee(order.getInviteeFee());

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_FAST);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);

//        String remark = String.format("被推荐人闪兑 refOrderId=%s,coinUnit=%s,tradedAmount=%s,tradedTurnover=%s,fee=%s",
//                order.getRefOrderId(), order.getCoinSymbol(), order.getTradedAmount(), order.getTradedTurnover(), order.getInviteeFee());
        record.setRemark("被推荐人闪兑成功");

        record.setCreateTime(Calendar.getInstance().getTime());
        return record;
    }

    // 退回冻结
    private Optional<ExchangeWalletWalRecord> buildGivebackWal(ExchangeReleaseReferrerOrder order, boolean isInviter) {

        // 计算增加数量和手续费
        BigDecimal amount = isInviter ? order.getFreezeAmount().subtract(order.getTradedAmount())
                : order.getInviteeFreezeAmount().subtract(order.getTradedTurnover());

        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            return Optional.empty();
        }

        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        record.setMemberId(isInviter ? order.getInviterId() : order.getInviteeId());
        record.setRefId(order.getRefOrderId());

        // 被推荐人，退还 交易币
        if (isInviter) {
            record.setCoinUnit(order.getCoinSymbol());
        } else {
            // 推荐人，退还 结算币
            record.setCoinUnit(order.getBaseSymbol());
        }

        // - 冻结
        record.setTradeFrozen(WalletUtils.negativeOf(amount));
        // + 可用
        record.setTradeBalance(WalletUtils.positiveOf(amount));

        record.setFee(BigDecimal.ZERO);

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_FAST);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);

        if (isInviter) {
            record.setRemark("推荐人闪兑退回冻结");
        } else {
            record.setRemark("被推荐人闪兑退回冻结");
        }

        record.setCreateTime(Calendar.getInstance().getTime());

        return Optional.of(record);
    }

    @Autowired
    public void setWalletOperations(ExchangeWalletOperations walletOperations) {
        this.walletOperations = walletOperations;
    }

    public ExchangeReleaseReferrerOrderServiceImpl getService() {
        return SpringContextUtil.getBean(ExchangeReleaseReferrerOrderServiceImpl.class);
    }

    private BigDecimal getExchangeRate() {
        // 闪兑手续费比例
        return globalParamService.getExchangeRate();
    }
}