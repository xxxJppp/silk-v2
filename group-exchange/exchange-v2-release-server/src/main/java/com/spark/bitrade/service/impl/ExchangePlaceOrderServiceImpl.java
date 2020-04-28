package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constant.ExchangeReleaseConstants;
import com.spark.bitrade.constant.ExchangeReleaseMsgCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.ExchangeOrderSellStat;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeReleaseFreezeRule;
import com.spark.bitrade.entity.TradePlateItem;
import com.spark.bitrade.lock.Callback;
import com.spark.bitrade.lock.DistributedLockTemplate;
import com.spark.bitrade.service.ExchangeReleaseFreezeRuleService;
import com.spark.bitrade.service.IExchangePlate;
import com.spark.bitrade.service.SellService;
import com.spark.bitrade.service.optfor.RedisStringService;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 *  下单服务
 *
 * @author young
 * @time 2019.11.11 17:59
 */
@Slf4j
@Service
public class ExchangePlaceOrderServiceImpl extends AbstractExchangePlaceOrderServiceImpl {
    @Autowired
    private ExchangeReleaseFreezeRuleService releaseFreezeRuleService;
    @Autowired
    private IExchangePlate exchangePlate;
    @Autowired
    private SellService sellService;
    @Autowired
    protected DistributedLockTemplate lockTemplate;

    @Override
    public MessageRespResult<ExchangeOrder> place(Long memberId, ExchangeOrderDirection direction,
                                                  String symbol, BigDecimal price, BigDecimal amount,
                                                  ExchangeOrderType type, String tradeCaptcha) {
        // 价格和数量合法性校验
        MessageRespResult respResult0 = this.checkLegal4PriceAndAmount(price, amount, type);
        if (!respResult0.isSuccess()) {
            return respResult0;
        }

        Optional<ExchangeReleaseFreezeRule> ruleOptional = releaseFreezeRuleService.findBySymbol(symbol);

        // 获取并校验交易对信息
        MessageRespResult<ExchangeCoin> respResult1 = this.getAndCheckExchangeCoin(symbol, tradeCaptcha);
        if (!respResult1.isSuccess()) {
            return this.message(respResult1);
        }

        // 验证并构建订单
        MessageRespResult<ExchangeOrder> respResultOrder = this.checkBuidlerOrder(memberId, direction, symbol,
                price, amount, type, respResult1.getData());
        if (!respResultOrder.isSuccess()) {
            return this.message(respResultOrder);
        }

        if (direction.equals(ExchangeOrderDirection.SELL)) {
            // 卖单
            // 检验 卖出价格必须大于等于卖1价格或者大于等于卖1价格+0.01USDT
            BigDecimal expectMinSell1Price = this.getExpectMinSell1Price(respResult1.getData());

            ///log.info("----price----{},{}", price, respResultOrder.getData().getPrice());

            return lockTemplate.execute(this.getLockId(symbol, respResultOrder.getData().getPrice()), 5, new Callback<MessageRespResult>() {
                @Override
                public MessageRespResult onGetLock() throws Exception {
                    // 卖单校验
                    MessageRespResult resultCheckSellOrder = checkSellOrder(symbol, respResultOrder.getData().getPrice(), amount, ruleOptional.get(), respResult1.getData(), expectMinSell1Price);
                    if (!resultCheckSellOrder.isSuccess()) {
                        return message(resultCheckSellOrder);
                    }

                    // 保存订单
                    ExchangeOrder order = exchangeOrderService.createOrder(respResultOrder.getData());

//                    /*// 下单成功后 清理缓存
//                    if (direction.equals(ExchangeOrderDirection.SELL)) {
//                        log.warn("下单成功后 清理缓存");
//                        sellService.cleanCached(symbol, price.stripTrailingZeros());
//                    }*/

                    return message(CommonMsgCode.SUCCESS, order);
                }

                @Override
                public MessageRespResult onTimeout() throws Exception {
                    return message(ExchangeReleaseMsgCode.FAILED_GET_LOCKED);
                }
            });
        } else {
            // 买单
            return this.message(CommonMsgCode.SUCCESS, exchangeOrderService.createOrder(respResultOrder.getData()));
        }
    }

    private MessageRespResult checkSellOrder(String symbol,
                                             BigDecimal price, BigDecimal amount,
                                             ExchangeReleaseFreezeRule rule,
                                             ExchangeCoin exchangeCoin, BigDecimal expectMinSell1Price) {

        // 检验 卖出价格必须大于等于卖1价格或者大于等于卖1价格+0.01USDT
        ///BigDecimal expectMinSell1Price = this.getExpectMinSell1Price(exchangeCoin);
        if (price.compareTo(expectMinSell1Price) < 0) {
            //提示： 卖出价必须等于卖1价或高于卖1价+0.01 USDT
            log.warn("提示：卖出价必须等于卖1价或高于卖1价+0.01 USDT。price={}", price);
            return this.message(ExchangeReleaseMsgCode.MUST_GREATER_SELL1,
                    String.valueOf(expectMinSell1Price.add(rule.getSellMinIncrement()).doubleValue()).concat(" ").concat(exchangeCoin.getBaseSymbol()));
        }

        // 剩余数量 默认为 最大交易数量
        BigDecimal remainAmount = rule.getSellMaxTradeAmount();

        // 校验 每个卖出价格的挂单+成交总数量上限为50000个
        Optional<ExchangeOrderSellStat> OptionalOrderSellStat = sellService.sellStatByPrice(symbol, price);
        if (OptionalOrderSellStat.isPresent()) {
            // 剩余数量 = 最大交易数量 - 已挂单、交易的数量
            remainAmount = rule.getSellMaxTradeAmount().subtract(OptionalOrderSellStat.get().getAmount());
        }

        // 校验 剩余数量 是否 超过限制
        if (BigDecimalUtil.lte0(remainAmount)) {
            // 提示： 该卖价已达到挂单和成交量上限，请更换价格重试
            log.warn("提示：该卖价已达到挂单和成交量上限，请更换价格重试 。price={}", price);
            return this.message(ExchangeReleaseMsgCode.CHANGE_PRICE);
        }

        // 校验 剩余数量 是否少于 最低挂单数量
        if (BigDecimalUtil.lt0(remainAmount.subtract(exchangeCoin.getMinAmount()))) {
            // 提示： 该卖价已达到挂单和成交量上限，请更换价格重试
            log.warn("提示： 该卖价已达到挂单和成交量上限，请更换价格重试 。price={}", price);
            return this.message(ExchangeReleaseMsgCode.CHANGE_PRICE);
        }

        // 校验 剩余数量 是否充足
        if (BigDecimalUtil.lt0(remainAmount.subtract(amount))) {
            // 提示： 您对该卖价的挂单量已超出最大挂单和成交量上限，您最多可挂xxx个
            log.warn("提示： 您对该卖价的挂单量已超出最大挂单和成交量上限，您最多可挂{}个", remainAmount);
            return this.message(ExchangeReleaseMsgCode.REMAIN_TRADE_AMOUNT, remainAmount);
        }

        return MessageRespResult.success();
    }

    @Override
    protected String getOrderId(Long memberId) {
        return new StringBuilder(ExchangeReleaseConstants.ORDER_PREFIX).append(memberId).append(IdWorker.getId()).toString();
    }

    /**
     * 获取盘口卖1价格
     *
     * @param symbol 交易对
     * @return
     */
    private Optional<BigDecimal> getPlateSell1Price(String symbol) {
        try {
            MessageRespResult<TradePlateItem> respResult = exchangePlate.tradePlateSell1(symbol);
            if (respResult.isSuccess() && Objects.nonNull(respResult.getData())) {
                return Optional.ofNullable(respResult.getData().getPrice());
            }
        } catch (Exception ex) {
            log.error("获取卖1价格失败", ex);
        }

        return Optional.empty();
    }

    /**
     * 最新的卖1价格
     *
     * @param symbol 交易对
     * @return
     */
    private Optional<BigDecimal> getSell1NewestPrice(String symbol) {
        //从redis 获取最新的成交价格
        return sellService.getSell1NewestPrice(symbol);
    }

    /**
     * 更新最新的卖1价格
     *
     * @param symbol 交易对
     * @param price  价格
     */
    private void updateSell1NewestPrice(String symbol, BigDecimal price) {
        sellService.updateSell1NewestPrice(symbol, price);
    }


    /**
     * 获取预期的卖1价格
     *
     * @param exchangeCoin
     * @return
     */
    private BigDecimal getExpectMinSell1Price(ExchangeCoin exchangeCoin) {
        // 最新卖1价格
        Optional<BigDecimal> optionalSell1NewestPrice = this.getSell1NewestPrice(exchangeCoin.getSymbol());

        // 获取盘口卖1价格
        Optional<BigDecimal> optionalSell1Price = this.getPlateSell1Price(exchangeCoin.getSymbol());
        if (optionalSell1Price.isPresent()) {
            // 更新最新卖1价格 到redis中
//            if (!optionalSell1NewestPrice.isPresent()
//                    || optionalSell1Price.get().compareTo(optionalSell1NewestPrice.get()) > 0) {
//                log.info("更新最新卖1价格 到redis中，oldprice={}, new price={}",
//                        optionalSell1NewestPrice.isPresent() ? optionalSell1NewestPrice.get() : BigDecimal.ZERO,
//                        optionalSell1Price.get());
//                this.updateSell1NewestPrice(exchangeCoin.getSymbol(), optionalSell1Price.get());
//            }

            // 卖1价格存在，获取卖1价格
            log.info("卖1价格存在，获取卖1价格 {}", optionalSell1Price.get());
            return optionalSell1Price.get();
        } else {
            // 卖1价格不存在：1）第一次，卖1必须大于1USDT；2）卖盘已空，获取不到卖1价格
            if (optionalSell1NewestPrice.isPresent()) {
                // 卖盘已空，获取不到卖1价格，使用最近的卖1价格
                log.info("卖盘已空，获取不到卖1价格，使用最近的卖1价格 {}", optionalSell1NewestPrice.get());
                return optionalSell1NewestPrice.get();
            } else {
                // 第一次，卖1必须大于1USDT（使用默认配置的卖1价）
                log.info("第一次，卖1必须大于1USDT（使用默认配置的卖1价） {}", exchangeCoin.getMinSellPrice());
                this.updateSell1NewestPrice(exchangeCoin.getSymbol(), exchangeCoin.getMinSellPrice());
                return exchangeCoin.getMinSellPrice();
            }
        }
    }

    /**
     * 获取分布式锁ID
     */
    protected String getLockId(String symbol, BigDecimal price) {
        return new StringBuilder("lock:sell:")
                .append(symbol)
                .append("-")
                .append(price.stripTrailingZeros())
                .toString();
    }
}
