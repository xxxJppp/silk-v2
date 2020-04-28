package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constant.ReferrerOrderStatus;
import com.spark.bitrade.dsc.DscContext;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.*;
import com.spark.bitrade.uitl.WalletUtils;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * 币币订单表服务实现类
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
@Slf4j
@Service("exchangeOrderService")
public class ExchangeOrderServiceImpl extends AbstractExchangeOrderServiceImpl {
    @Autowired
    private IMemberApiService memberApiService;
    @Autowired
    private ExchangeWalletOperations exchangeWalletOperations;
    @Autowired
    private ExchangeRateService rateService;
    @Autowired
    private DscContext dscContext;
    @Autowired
    private ExchangeReleaseReferrerOrderService referrerOrderService;
    @Autowired
    private GlobalParamService globalParamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangeOrder placeOrder(ExchangeOrder exchangeOrder) {
        // 买单时，推荐人有ESP余额，则冻结ESP并生成闪兑订单、冻结流水记录
        if (exchangeOrder.getDirection().equals(ExchangeOrderDirection.BUY)) {
            MessageRespResult<Member> result = memberApiService.getMember(exchangeOrder.getMemberId());
            if (result.isSuccess() && Objects.nonNull(result.getData().getInviterId())) {
                // 判断推荐人是否有余额
                Optional<ExchangeWallet> optional =
                        this.exchangeWalletOperations.balance(result.getData().getInviterId(), exchangeOrder.getCoinSymbol());
                if (optional.isPresent() && BigDecimalUtil.gt0(optional.get().getBalance())) {
                    /*1、判断推荐人有余额时，记录买入订单的"币币交易-推荐人闪兑订单表"记录
                    2、冻结推荐人的闪兑币种的余额
                    3、生成推荐人闪兑冻结账户流的水记录
                    4、冻结被推荐人的基本余额
                    5、生成被推荐人闪兑冻结账户流的水记录*/

                    // 计算闪兑数量(eg：ESP)
                    BigDecimal exchangeAmount = BigDecimalUtil.mul2down(exchangeOrder.getAmount(), this.getBuyMaxExchangeRatio(),
                            exchangeOrder.getAmount().scale());
                    if (optional.get().getBalance().compareTo(exchangeAmount) < 0) {
                        exchangeAmount = optional.get().getBalance();
                    }

                    // 被推荐人闪兑冻结的币数（eg：USDT）
                    BigDecimal inviteeFreezeAmount = BigDecimalUtil.mul2up(exchangeAmount, exchangeOrder.getPrice(),
                            exchangeOrder.getFreezeAmount().scale());

                    // 构建 推荐人闪兑订单记录
                    ExchangeReleaseReferrerOrder referrerOrder =
                            builderExchangeReleaseReferrerOrder(exchangeOrder, result.getData().getInviterId(),
                                    exchangeAmount, inviteeFreezeAmount);

                    // 推荐人冻结记录 todo 余额不够的情况,需要调整为“推荐人余额不够”的提示？？
                    ExchangeWalletWalRecord inviterFreezeWalletWalRecord = builderInviterFreezeWalletWalRecord(referrerOrder);
                    exchangeWalletOperations.booking(inviterFreezeWalletWalRecord);

                    // 被推荐人冻结记录
                    ExchangeWalletWalRecord inviteeFreezeWalletWalRecord = builderInviteeFreezeWalletWalRecord(referrerOrder);
                    exchangeWalletOperations.booking(inviteeFreezeWalletWalRecord);


                    // 保存 被推荐人闪兑记录
                    referrerOrderService.save(referrerOrder);

                    // 更新订单数据
                    exchangeOrder.setAmount(referrerOrder.getRefPlaceAmount());
                    exchangeOrder.setFreezeAmount(this.calculateFreeAmount(exchangeOrder));
                } else {
                    log.info("不满足闪兑要求：result={}", optional);
                }
            } else {
                log.info("不满足推荐关系：result={}, inviterId={}", result.isSuccess(), result.getData().getInviterId());
            }
        }

        return super.placeOrder(exchangeOrder);
    }

    private BigDecimal getBuyMaxExchangeRatio() {
        // 买单最大闪兑比例
        return globalParamService.getBuyMaxExchangeRatio();
    }

    /**
     * 计算冻结数量
     *
     * @param order 订单信息
     */
    protected BigDecimal calculateFreeAmount(ExchangeOrder order) {
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            BigDecimal turnover;
            if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
                turnover = order.getAmount();
            } else {
                // 设置计划交易额的精度，应该使用价格精度
                turnover = order.getAmount().multiply(order.getPrice())
                        .setScale(order.getFreezeAmount().scale(), BigDecimal.ROUND_UP);
            }
            return turnover;
        } else {
            return order.getAmount();
        }
    }

    /**
     * 构建 推荐人闪兑订单记录
     *
     * @param exchangeOrder       订单信息
     * @param InviterId           推荐人
     * @param exchangeAmount      闪兑币数
     * @param inviteeFreezeAmount 被推荐人冻结数量
     * @return
     */
    private ExchangeReleaseReferrerOrder builderExchangeReleaseReferrerOrder(ExchangeOrder exchangeOrder,
                                                                             Long InviterId,
                                                                             BigDecimal exchangeAmount,
                                                                             BigDecimal inviteeFreezeAmount) {
        ExchangeReleaseReferrerOrder referrerOrder = new ExchangeReleaseReferrerOrder();
        referrerOrder.setRefOrderId(exchangeOrder.getOrderId());
        referrerOrder.setRefSymbol(exchangeOrder.getSymbol());
        referrerOrder.setCoinSymbol(exchangeOrder.getCoinSymbol());
        referrerOrder.setBaseSymbol(exchangeOrder.getBaseSymbol());
        referrerOrder.setRefAmount(exchangeOrder.getAmount());
        referrerOrder.setRefPlaceAmount(exchangeOrder.getAmount().subtract(exchangeAmount));
        referrerOrder.setInviteeFreezeAmount(inviteeFreezeAmount);
        referrerOrder.setInviterId(InviterId);
        referrerOrder.setInviteeId(exchangeOrder.getMemberId());
        referrerOrder.setStatus(ReferrerOrderStatus.TRADING);
        ///referrerOrder.setRate();
        referrerOrder.setFreezeAmount(exchangeAmount);
        ///referrerOrder.setTradedAmount();
        ///referrerOrder.setTradedTurnover();
        ///referrerOrder.setInviterFee();
        ///referrerOrder.setInviteeFee();
        //referrerOrder.setCreateTime();
        ///referrerOrder.setUpdateTime();
        return referrerOrder;
    }

    /**
     * 构建被推荐人USDT的冻结记录
     *
     * @return
     */
    private ExchangeWalletWalRecord builderInviteeFreezeWalletWalRecord(ExchangeReleaseReferrerOrder referrerOrder) {
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        record.setMemberId(referrerOrder.getInviteeId());
        record.setRefId(referrerOrder.getRefOrderId());
        record.setCoinUnit(referrerOrder.getBaseSymbol());

        // - 余额
        record.setTradeBalance(WalletUtils.negativeOf(referrerOrder.getInviteeFreezeAmount()));
        // + 冻结
        record.setTradeFrozen(WalletUtils.positiveOf(referrerOrder.getInviteeFreezeAmount()));
        record.setTradeType(WalTradeType.FREEZE);

        // 手续费
        record.setFeeDiscount(BigDecimal.ZERO);
        record.setFee(BigDecimal.ZERO);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        record.setCreateTime(new Date());
        record.setSyncId(0L);

        record.setRate(rateService.gateUsdRate(record.getCoinUnit()));
        record.setRemark("被推荐人闪兑冻结");

        dscContext.getDscEntityResolver(record).update();

        return record;
    }

    /**
     * 构建推荐人ESP的冻结记录
     *
     * @return
     */
    private ExchangeWalletWalRecord builderInviterFreezeWalletWalRecord(ExchangeReleaseReferrerOrder referrerOrder) {
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        record.setMemberId(referrerOrder.getInviterId());
        record.setRefId(referrerOrder.getRefOrderId());
        record.setCoinUnit(referrerOrder.getCoinSymbol());

        // - 余额
        record.setTradeBalance(WalletUtils.negativeOf(referrerOrder.getFreezeAmount()));
        // + 冻结
        record.setTradeFrozen(WalletUtils.positiveOf(referrerOrder.getFreezeAmount()));
        record.setTradeType(WalTradeType.FREEZE);

        // 手续费
        record.setFeeDiscount(BigDecimal.ZERO);
        record.setFee(BigDecimal.ZERO);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        record.setSyncId(0L);
        record.setCreateTime(new Date());

        record.setRate(rateService.gateUsdRate(record.getCoinUnit()));
        record.setRemark("推荐人闪兑冻结");

        dscContext.getDscEntityResolver(record).update();
        return record;
    }
}