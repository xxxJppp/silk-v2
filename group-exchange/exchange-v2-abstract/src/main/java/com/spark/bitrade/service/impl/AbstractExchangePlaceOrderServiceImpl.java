package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberSecuritySet;
import com.spark.bitrade.entity.constants.ExchangeConstants;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 *  下单服务
 *
 * @author young
 * @time 2019.11.11 17:59
 */
@Slf4j
public abstract class AbstractExchangePlaceOrderServiceImpl implements ExchangePlaceOrderService {
    //    @Resource
//    protected OrderFacadeService orderService;
    @Autowired
    protected IMemberApiService memberApiService;
    @Autowired
    protected ExchangeOrderService exchangeOrderService;
    @Autowired
    protected ExchangeCoinService exchangeCoinService;

    @Override
    public MessageRespResult<ExchangeOrder> place(Long memberId, ExchangeOrderDirection direction, String symbol, BigDecimal price, BigDecimal amount, ExchangeOrderType type, String tradeCaptcha) {
        // 获取订单预下单信息
        MessageRespResult<ExchangeOrder> respResult = this.prePlace(memberId, direction, symbol,
                price, amount, type, tradeCaptcha);
        if (!respResult.isSuccess()) {
            return respResult;
        }

        // 创建并返回订单信息
        return this.message(CommonMsgCode.SUCCESS, this.exchangeOrderService.createOrder(respResult.getData()));
    }

    /**
     * 预下单
     *
     * @param memberId
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type
     * @param tradeCaptcha
     * @return
     */
    protected MessageRespResult<ExchangeOrder> prePlace(Long memberId, ExchangeOrderDirection direction, String symbol, BigDecimal price, BigDecimal amount, ExchangeOrderType type, String tradeCaptcha) {
        // 价格和数量合法性校验
        MessageRespResult respResult0 = this.checkLegal4PriceAndAmount(price, amount, type);
        if (!respResult0.isSuccess()) {
            return respResult0;
        }

        // 获取并校验交易对信息
        MessageRespResult<ExchangeCoin> respResult1 = this.getAndCheckExchangeCoin(symbol, tradeCaptcha);
        if (!respResult1.isSuccess()) {
            return this.message(respResult1);
        }

        // 验证并构建订单
        return this.checkBuidlerOrder(memberId, direction, symbol,
                price, amount, type, respResult1.getData());
    }

    /**
     * 获取订单ID
     *
     * @param memberId
     * @return
     */
    protected String getOrderId(Long memberId) {
        return new StringBuilder(ExchangeConstants.ORDER_PREFIX).append(memberId).append(IdWorker.getId()).toString();
    }

    /**
     * 价格和数量合法性校验
     *
     * @param price
     * @param amount
     * @param type
     * @return
     */
    protected MessageRespResult<MsgCode> checkLegal4PriceAndAmount(BigDecimal price, BigDecimal amount, ExchangeOrderType type) {
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return this.message(ExchangeOrderMsgCode.ILLEGAL_PRICE);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return this.message(ExchangeOrderMsgCode.ILLEGAL_QUANTITY);
        }

        return MessageRespResult.success();
    }

    /**
     * 获取并校验交易对信息
     *
     * @param symbol
     * @return
     */
    protected MessageRespResult<ExchangeCoin> getAndCheckExchangeCoin(String symbol, String tradeCaptcha) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null || exchangeCoin.getEnable() != 1
                || exchangeCoin.getSymbol().equalsIgnoreCase(symbol) == false) {
            log.error("不支持的交易对。交易对={},交易对配置={}", symbol, exchangeCoin);
            return this.message(ExchangeOrderMsgCode.UNSUPPORTED);
        }

        // 验证交易码
        if (StringUtils.hasText(exchangeCoin.getTradeCaptcha())
                && !exchangeCoin.getTradeCaptcha().equalsIgnoreCase(tradeCaptcha)) {
            return this.message(ExchangeOrderMsgCode.INVALID_TRADE_CAPTCHA);
        }

        return MessageRespResult.success4Data(exchangeCoin);
    }

    /**
     * 用户状态校验
     *
     * @param memberId
     * @return
     */
    protected MessageRespResult checkUserStatus(long memberId) {
        // 用户状态及交易状态判断
        MessageRespResult<Member> result = memberApiService.getMember(memberId);
        if (!result.isSuccess()) {
            return result;
        }

        Member memberNow = result.getData();
        AssertUtil.notNull(memberNow, ExchangeOrderMsgCode.ILLEGAL_USER);
        AssertUtil.isTrue(memberNow.getStatus() == null || memberNow.getStatus() == CommonStatus.NORMAL,
                ExchangeOrderMsgCode.ACCOUNT_DISABLE);
        AssertUtil.isTrue(memberNow.getTransactionStatus() == null || memberNow.getTransactionStatus() == BooleanEnum.IS_TRUE,
                ExchangeOrderMsgCode.LIMIT_TRAD);

        this.validateOpenBBTransaction(memberId);

        return MessageRespResult.success();
    }

    /**
     * 验证是否允许币币交易
     *
     * @param memberId
     */
    protected void validateOpenBBTransaction(long memberId) {
        MessageRespResult<MemberSecuritySet> result = memberApiService.getMemberSecuritySet(memberId);
        if (result != null && result.getData() != null) {
            AssertUtil.isTrue(result.getData().getIsOpenBbTransaction().isIs(), ExchangeOrderMsgCode.LIMIT_TRAD);
        }
    }

    /**
     * 计算冻结数量
     *
     * @param order        订单信息
     * @param exchangeCoin
     */
    protected void calculateFreeAmount(ExchangeOrder order, final ExchangeCoin exchangeCoin) {
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            BigDecimal turnover;
            if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
                turnover = order.getAmount();
            } else {
                // 设置计划交易额的精度，应该使用价格精度
                turnover = order.getAmount().multiply(order.getPrice())
                        .setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_UP);
            }
            // 记录冻结数量
            order.setFreezeAmount(turnover);
        } else {
            // 记录冻结数量
            order.setFreezeAmount(order.getAmount());
        }
    }

    /**
     * 交易对限制校验
     *
     * @param order
     * @param exchangeCoin
     * @return
     */
    protected MessageRespResult checkExchangeCoinLimit(ExchangeOrder order, final ExchangeCoin exchangeCoin) {
        // 查看是否启用市价买卖
        if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
            if (order.getDirection() == ExchangeOrderDirection.BUY && exchangeCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE) {
                return this.message(ExchangeOrderMsgCode.NOT_SUPPORT_BUY);
            } else if (order.getDirection() == ExchangeOrderDirection.SELL && exchangeCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE) {
                return this.message(ExchangeOrderMsgCode.NOT_SUPPORT_SELL);
            }
            // 市价的价格为0
            order.setPrice(BigDecimal.ZERO);
        } else {
            // 限价卖出,如果有最低卖价限制，出价不能低于此价
            if (order.getDirection() == ExchangeOrderDirection.SELL && exchangeCoin.getMinSellPrice().compareTo(BigDecimal.ZERO) > 0
                    && order.getPrice().compareTo(exchangeCoin.getMinSellPrice()) < 0) {
                // 提供具体的数据  exchangeCoin.getMinSellPrice()
                return this.message(ExchangeOrderMsgCode.CANNOT_LOWER, exchangeCoin.getMinSellPrice());
            }
        }

        // 限制委托数量
        if (exchangeCoin.getMaxTradingOrder() > 0
                && exchangeOrderService.findCurrentTradingCount(order.getMemberId(), order.getSymbol(), order.getDirection()) >= exchangeCoin.getMaxTradingOrder()) {

            return this.message(ExchangeOrderMsgCode.MAXIMUM_TRADING_LIMIT, exchangeCoin.getMaxTradingOrder());
        }

        return this.message(CommonMsgCode.SUCCESS);
    }

    /**
     * 处理精度
     *
     * @param price        输入的单价
     * @param amount       输入的数量
     * @param order
     * @param exchangeCoin
     * @return
     */
    protected MessageRespResult handlePrecision(BigDecimal price, BigDecimal amount,
                                                ExchangeOrder order, final ExchangeCoin exchangeCoin) {
        // 处理价格精度及重新验证调整后的价格
        order.setPrice(price.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN));
        if (order.getType() == ExchangeOrderType.LIMIT_PRICE && order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return this.message(ExchangeOrderMsgCode.ILLEGAL_PRICE);
        }

        // 处理数量精度及重新验证调整后的数量
        if (order.getDirection() == ExchangeOrderDirection.BUY && order.getType() == ExchangeOrderType.MARKET_PRICE) {
            order.setAmount(amount.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN));
            if (order.getAmount().compareTo(exchangeCoin.getMinTurnover()) < 0) {
                // 提供具体的数据  exchangeCoin.getMinTurnover()
                return this.message(ExchangeOrderMsgCode.TURNOVER_LIMIT, exchangeCoin.getMinTurnover());
            }
        } else {
            order.setAmount(amount.setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_DOWN));

            // 最少委托数量限制
            if(order.getDirection() == ExchangeOrderDirection.BUY) {
                if (order.getAmount().compareTo(exchangeCoin.getMinAmount()) < 0) {
                    // 提供具体的数据  exchangeCoin.getMinAmount()
                    return this.message(ExchangeOrderMsgCode.NUMBER_LIMIT, exchangeCoin.getMinAmount());
                }
            } else {
                if (order.getAmount().compareTo(exchangeCoin.getMinSellAmount()) < 0) {
                    return this.message(ExchangeOrderMsgCode.NUMBER_LIMIT, exchangeCoin.getMinSellAmount());
                }
            }
        }
        if (order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return this.message(ExchangeOrderMsgCode.ILLEGAL_QUANTITY);
        }
        return this.message(CommonMsgCode.SUCCESS);
    }


    /**
     * 验证、构建订单
     *
     * @param memberId
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type
     * @param exchangeCoin
     * @return
     */
    protected MessageRespResult<ExchangeOrder> checkBuidlerOrder(Long memberId, ExchangeOrderDirection direction, String symbol,
                                                                 BigDecimal price, BigDecimal amount, ExchangeOrderType type, ExchangeCoin exchangeCoin) {
        // 订单
        ExchangeOrder order = new ExchangeOrder();
        order.setMemberId(memberId);
        order.setType(type);
        order.setSymbol(symbol);
        order.setDirection(direction);
        order.setBaseSymbol(exchangeCoin.getBaseSymbol());
        order.setCoinSymbol(exchangeCoin.getCoinSymbol());
        log.info("exCoin={}, baseCoin={}, direction={}, type={}",
                exchangeCoin.getCoinSymbol(), exchangeCoin.getBaseSymbol(), order.getDirection(), order.getType());

        // 校验用户状态
        MessageRespResult respResult1 = this.checkUserStatus(memberId);
        if (!respResult1.isSuccess()) {
            return respResult1;
        }

        // todo 接入风控

        // 处理价格和数量的精度及校验处理后的结果
        MessageRespResult respResult2 = this.handlePrecision(price, amount, order, exchangeCoin);
        if (!respResult2.isSuccess()) {
            return respResult2;
        }

        // 交易对限制校验
        MessageRespResult respResult3 = this.checkExchangeCoinLimit(order, exchangeCoin);
        if (!respResult3.isSuccess()) {
            return respResult3;
        }

        // 计算冻结数量
        this.calculateFreeAmount(order, exchangeCoin);

        // 生成订单ID
        order.setOrderId(this.getOrderId(memberId));
        order.setTime(System.currentTimeMillis());
        order.setStatus(ExchangeOrderStatus.TRADING);
        order.setTradedAmount(BigDecimal.ZERO);

        return MessageRespResult.success4Data(order);
    }

    protected <T> MessageRespResult<T> message(MsgCode code, T data) {
        return new MessageRespResult<>(code.getCode(), code.getMessage(), data);
    }

    protected <T> MessageRespResult<T> message(MsgCode code) {
        return new MessageRespResult<>(code.getCode(), code.getMessage());
    }

    protected <T> MessageRespResult<T> message(MessageRespResult result) {
        return new MessageRespResult(result.getCode(), result.getMessage(), result.getData());
    }
}
