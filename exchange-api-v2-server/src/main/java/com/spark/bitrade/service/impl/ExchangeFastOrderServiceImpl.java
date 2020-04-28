package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeMsgCode;
import com.spark.bitrade.entity.ExchangeFastAccount;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.entity.ExchangeFastOrder;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.ExchangeFastOrderMapper;
import com.spark.bitrade.service.ExchangeFastAccountService;
import com.spark.bitrade.service.ExchangeFastCoinService;
import com.spark.bitrade.service.ExchangeFastOrderService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.trans.ExchangeFastCoinRateInfo;
import com.spark.bitrade.trans.WalletBaseEntity;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 闪兑订单(ExchangeFastOrder)表服务实现类
 *
 * @author yangch
 * @since 2019-06-24 17:06:54
 */
@Slf4j
@Service("exchangeFastOrderService")
public class ExchangeFastOrderServiceImpl extends ServiceImpl<ExchangeFastOrderMapper, ExchangeFastOrder> implements ExchangeFastOrderService {
    @Autowired
    private ExchangeFastCoinService fastCoinService;

    @Autowired
    private ExchangeFastAccountService fastAccountService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    @Autowired(required = false)
    private IdWorkByTwitter idWorkByTwitterSnowflake;


    @Override
    @Cacheable(cacheNames = "exchangeFastOrder", key = "'entity:exchangeFastOrder:'+ #orderId")
    public ExchangeFastOrder findOne(Long orderId) {
        return this.baseMapper.selectById(orderId);
    }

    /**
     * 闪兑发起方接口
     *
     * @param memberId       会员ID
     * @param appId          应用ID
     * @param coinSymbol     闪兑币种名称
     * @param baseSymbol     闪兑基币名称
     * @param amount         闪兑数量
     * @param direction      兑换方向
     * @param rateInfo       汇率信息
     * @param isTargetAmount 是否兑换为指定目标结果的币
     * @return
     */
    @Override
    public ExchangeFastOrder exchangeInitiator(Long memberId, String appId,
                                               String coinSymbol, String baseSymbol,
                                               BigDecimal amount, ExchangeOrderDirection direction,
                                               ExchangeFastCoinRateInfo rateInfo,
                                               boolean isTargetAmount) {

        log.info("闪兑发起方===开始：memberId={},appId={}, coinSymbol={}, baseSymbol={}," +
                        " amount={}, direction{},isTargetAmount={}, rateInfo={}",
                memberId, appId, coinSymbol, baseSymbol, amount, direction, isTargetAmount, rateInfo);

        // 1、校验输入参数
        AssertUtil.notNull(memberId, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(coinSymbol, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(baseSymbol, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(amount, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(direction, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(rateInfo, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.isTrue(BigDecimalUtil.gt0(amount), ExchangeMsgCode.INVALID_EXCHANGE_AMOUNT);

        //币种大小写兼容
        if (StringUtils.hasText(coinSymbol)) {
            coinSymbol = coinSymbol.toUpperCase();
        }
        if (StringUtils.hasText(baseSymbol)) {
            baseSymbol = baseSymbol.toUpperCase();
        }

        // 2、获取闪兑汇率，并验证汇率不能为0
        AssertUtil.isTrue(BigDecimalUtil.gt0(rateInfo.getRealtimeRate()), ExchangeMsgCode.INVALID_EXCHANGE_RATE);
        AssertUtil.isTrue(BigDecimalUtil.gt0(rateInfo.getTradeRate()), ExchangeMsgCode.INVALID_EXCHANGE_RATE);

        // 3、验证币种是否支持闪兑
        ExchangeFastCoin exchangeFastCoin = fastCoinService.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);
        AssertUtil.notNull(exchangeFastCoin, ExchangeMsgCode.NONSUPPORT_FAST_EXCHANGE_COIN);
        AssertUtil.notNull(fastCoinService.list4BaseSymbol(appId).contains(baseSymbol),
                ExchangeMsgCode.NONSUPPORT_FAST_EXCHANGE_COIN);

        // 4、验证闪兑用户是否存在
        ExchangeFastAccount fastAccount = fastAccountService.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);
        AssertUtil.notNull(fastAccount, ExchangeMsgCode.MISSING_FAST_EXCHANGE_ACCOUNT);

        // 5、构建闪兑订单
        ExchangeFastOrder order;
        if (isTargetAmount) {
            order = this.buildExchangeFastOrderByTargetAmount(memberId, appId, coinSymbol, baseSymbol,
                    amount, direction, rateInfo, fastAccount, exchangeFastCoin);
        } else {
            order = this.buildExchangeFastOrder(memberId, appId, coinSymbol, baseSymbol,
                    amount, direction, rateInfo, fastAccount, exchangeFastCoin);
        }

        //6、构建账户兑换交易信息
        WalletExchangeEntity exchangeEntity = this.buildInitiatorWalletExchangeEntity(order);


        // 7、通过事务 保存 订单、账户账户相关信息
        getService().doWithTransactional(order, exchangeEntity);

        // 8、异步调用或异步通知 兑换处理方处理总账户逻辑
        getService().asyncExchangeReceiver(order.getOrderId());

        log.info("闪兑发起方===结束:{}", order);
        return order;
    }


    /**
     * 通过分布式事务处理账户和闪兑订单数据
     * 备注：将保存的闪兑订单进行缓存，避免读写分离库延迟同步带来的查询不到结果的问题
     *
     * @param order
     * @param exchangeEntity
     */
//    @LcnTransaction
    @Transactional(rollbackFor = {Exception.class, MessageCodeException.class})
    @Cacheable(cacheNames = "exchangeFastOrder", key = "'entity:exchangeFastOrder:'+ #order.orderId")
    public ExchangeFastOrder doWithTransactional(ExchangeFastOrder order, WalletExchangeEntity exchangeEntity) {
        //扣除闪兑数量、添加闪兑获得币的数据
        MessageRespResult<Boolean> respResult = memberWalletApiService.exchange(exchangeEntity);
        if (!respResult.isSuccess()) {
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.of(respResult.getCode(), respResult.getMessage()));
        }

        //保存兑换订单
        if (!SqlHelper.retBool(this.baseMapper.insert(order))) {
            ExceptionUitl.throwsMessageCodeException(ExchangeMsgCode.SAVE_EXCHANGE_ORDER_FAILED);
        }

        return order;
    }


    /**
     * 闪兑接收方接口
     *
     * @param orderId 订单ID
     */
    @Override
    public void exchangeReceiver(Long orderId) {
        log.info("闪兑接收方===开始:orderId={}", orderId);
        // 1、校验输入的订单是否存在
        ExchangeFastOrder order = getService().findOne(orderId);
        AssertUtil.notNull(order, ExchangeMsgCode.NONEXISTENT_ORDER);

        // 2、验证“兑换发起方处理状态”状态是否为“完成”
        AssertUtil.isTrue(order.getInitiatorStatus() == ExchangeOrderStatus.COMPLETED, ExchangeMsgCode.UNMATCHED_STATUS);

        // 3、验证“兑换接收方处理状态”状态是否为“交易中”
        if (order.getReceiverStatus() == ExchangeOrderStatus.COMPLETED) {
            return;
        }

        //4、构建账户兑换交易信息
        WalletExchangeEntity exchangeEntity = this.buildReceiverWalletExchangeEntity(order);

        getService().doWithTransactional4Receiver(order, exchangeEntity);

        log.info("闪兑接收方===结束:orderId={}", orderId);
    }

    @Async
    public void asyncExchangeReceiver(Long orderId) {
//        //延迟处理，防止主从不同步
//        try {
//            Thread.sleep(500);
//        } catch (Exception ex) {
//        }
        this.exchangeReceiver(orderId);
    }

    /**
     * 通过分布式事务处理接收方
     * 更改状态后，清除 缓存
     *
     * @param order
     * @param exchangeEntity
     */
//    @LcnTransaction
    @Transactional(rollbackFor = {Exception.class, MessageCodeException.class})
    @CacheEvict(cacheNames = "exchangeFastOrder", key = "'entity:exchangeFastOrder:'+ #order.orderId")
    public void doWithTransactional4Receiver(ExchangeFastOrder order, WalletExchangeEntity exchangeEntity) {
        //扣除闪兑数量、添加闪兑获得币的数据
        MessageRespResult<Boolean> respResult = memberWalletApiService.exchange(exchangeEntity);
        if (!respResult.isSuccess()) {
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.of(respResult.getCode(), respResult.getMessage()));
        }

        //修改“闪兑订单.兑换接收方处理状态”为“完成”、并更新“成交时间”
        boolean flag = SqlHelper.retBool(this.baseMapper.updataReceiverStatus(order.getOrderId(),
                ExchangeOrderStatus.TRADING, ExchangeOrderStatus.COMPLETED, System.currentTimeMillis()));
        AssertUtil.isTrue(flag, ExchangeMsgCode.UPDATE_EXCHANGE_ORDER_STATUS_FAILED);

    }

    /**
     * 构建闪兑订单记录
     *
     * @param memberId    会员ID
     * @param coinSymbol  兑换币种
     * @param baseSymbol  兑换基币
     * @param amount      兑换数量
     * @param direction   兑换方向
     * @param rateInfo    汇率信息
     * @param fastAccount 闪兑总账户
     * @return 生成闪兑订单信息
     */
    private ExchangeFastOrder buildExchangeFastOrder(Long memberId, String appId, String coinSymbol,
                                                     String baseSymbol, BigDecimal amount,
                                                     ExchangeOrderDirection direction,
                                                     ExchangeFastCoinRateInfo rateInfo,
                                                     ExchangeFastAccount fastAccount, ExchangeFastCoin exchangeFastCoin) {
        ExchangeFastOrder order = buildBasicExchangeFastOrder(memberId, appId,
                coinSymbol, baseSymbol, direction, rateInfo, fastAccount);

        //设置数量
        order.setAmount(amount);

        //获取买卖的浮动比例
        BigDecimal adjustRate = getAdjustRate(direction, fastAccount, exchangeFastCoin);
        rateInfo.setBuyAdjustRate(adjustRate);
        rateInfo.setSellAdjustRate(adjustRate);

        //成交价，根据实时汇率、闪兑浮动比例以及方向计算出来的成交价
        ///tradedPrice = currentPrice.multiply(BigDecimal.ONE.subtract(adjustRate)).setScale(8, BigDecimal.ROUND_DOWN);
        BigDecimal tradedPrice = rateInfo.getTradeRate().setScale(8, BigDecimal.ROUND_DOWN);

        //成交数量
        ///tradedAmount = amount.multiply(tradedPrice).setScale(8, BigDecimal.ROUND_DOWN);
        BigDecimal tradedAmount = rateInfo.calculateTradeAmount(amount).setScale(8, BigDecimal.ROUND_DOWN);

        //虚拟佣金
        ///virtualBrokerageFee = amount.multiply(currentPrice).setScale(8, BigDecimal.ROUND_DOWN).subtract(tradedAmount);
        BigDecimal virtualBrokerageFee = rateInfo.calculateRealtimeAmount(amount).setScale(8, BigDecimal.ROUND_DOWN).subtract(tradedAmount);

        order.setAdjustRate(adjustRate);
        order.setTradedPrice(tradedPrice);
        order.setTradedAmount(tradedAmount);
        order.setVirtualBrokerageFee(virtualBrokerageFee);

        return order;
    }

    /**
     * 根据兑换目标数量构建闪兑订单记录
     *
     * @param memberId     会员ID
     * @param coinSymbol   兑换币种
     * @param baseSymbol   兑换基币
     * @param targetAmount 兑换目标数量
     * @param direction    兑换方向
     * @param rateInfo     汇率信息
     * @param fastAccount  闪兑总账户
     * @return 生成闪兑订单信息
     */
    private ExchangeFastOrder buildExchangeFastOrderByTargetAmount(Long memberId, String appId, String coinSymbol,
                                                                   String baseSymbol, BigDecimal targetAmount,
                                                                   ExchangeOrderDirection direction,
                                                                   ExchangeFastCoinRateInfo rateInfo,
                                                                   ExchangeFastAccount fastAccount, ExchangeFastCoin exchangeFastCoin) {

        ExchangeFastOrder order = buildBasicExchangeFastOrder(memberId, appId,
                coinSymbol, baseSymbol, direction, rateInfo, fastAccount);

        //成交数量
        order.setTradedAmount(targetAmount);

        //获取买卖的浮动比例
        BigDecimal adjustRate = getAdjustRate(direction, fastAccount, exchangeFastCoin);
        rateInfo.setBuyAdjustRate(adjustRate);
        rateInfo.setSellAdjustRate(adjustRate);

        //成交价，根据实时汇率、闪兑浮动比例以及方向计算出来的成交价
        BigDecimal tradedPrice = rateInfo.getTradeRate().setScale(8, BigDecimal.ROUND_DOWN);

        //计算 支付数量
        BigDecimal payAmount = rateInfo.calculateTradePayAmount(targetAmount).setScale(8, BigDecimal.ROUND_DOWN);

        //虚拟佣金
        BigDecimal virtualBrokerageFee = rateInfo.calculateRealtimeAmount(payAmount).setScale(8, BigDecimal.ROUND_DOWN).subtract(targetAmount);

        order.setAdjustRate(adjustRate);
        order.setTradedPrice(tradedPrice);
        order.setVirtualBrokerageFee(virtualBrokerageFee);
        order.setAmount(payAmount);

        return order;
    }

    //构建闪兑订单公共部分
    private ExchangeFastOrder buildBasicExchangeFastOrder(Long memberId, String appId,
                                                          String coinSymbol, String baseSymbol,
                                                          ExchangeOrderDirection direction,
                                                          ExchangeFastCoinRateInfo rateInfo,
                                                          ExchangeFastAccount fastAccount) {
        ExchangeFastOrder order = new ExchangeFastOrder();
        order.setOrderId(idWorkByTwitterSnowflake != null ? idWorkByTwitterSnowflake.nextId() : IdWorker.getId());
        order.setMemberId(memberId);
        order.setBaseSymbol(baseSymbol);
        order.setCoinSymbol(coinSymbol);
        order.setDirection(direction);
        order.setCurrentPrice(rateInfo.getRealtimeRate().setScale(8, BigDecimal.ROUND_DOWN));
        order.setInitiatorStatus(ExchangeOrderStatus.COMPLETED);
        order.setReceiverStatus(ExchangeOrderStatus.TRADING);
        order.setCreateTime(System.currentTimeMillis());
        order.setCompletedTime(System.currentTimeMillis());
        order.setReceiveId(fastAccount.getMemberId());
        order.setAppId(appId);

        ///order.setAmount(amount);
        return order;
    }


    private BigDecimal getAdjustRate(ExchangeOrderDirection direction, ExchangeFastAccount fastAccount, ExchangeFastCoin exchangeFastCoin) {
        BigDecimal adjustRate;
        if (direction == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //输入：
            //   基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            //   兑换数量(BT)：amount = 100 (用户输入)
            //   调整的比例：adjustRate = 0.05
            //计算：
            //  tradedPrice(成交价格) = currentPrice[0.25]*(1-adjustRate[0.05]) = 0.2375
            //  tradedAmount(成交数量BTC) = amount[100] * tradedPrice[0.2375] = 23.75 <25(正常)
            //  virtualBrokerageFee(虚拟佣金BTC) = amount[100] * currentPrice[0.25] - tradedAmount[23.75] = 1.25

            //如未配置浮动比例则获取默认配置
            if (!StringUtils.isEmpty(fastAccount.getBuyAdjustRate())) {
                adjustRate = fastAccount.getBuyAdjustRate();
            } else {
                adjustRate = StringUtils.isEmpty(exchangeFastCoin.getBuyAdjustRate())
                        ? BigDecimal.ZERO
                        : exchangeFastCoin.getBuyAdjustRate();
            }
        } else {
            //卖出场景：支付币种 ->兑换基币币种
            //输入：
            //   兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            //   兑换数量(BTC)：amount = 25 (用户输入)
            //   调整的比例：adjustRate = 0.05
            //计算：
            //  tradedPrice(成交价格) = currentPrice[4]*(1-adjustRate[0.05]) = 3.8
            //  tradedAmount(成交数量BT) = amount[25] * tradedPrice[3.8] = 95 < 100(正常)
            //  virtualBrokerageFee(虚拟佣金BT) = amount[25] * currentPrice[4] - tradedAmount[95] = 5

            //如未配置浮动比例则获取默认配置
            if (!StringUtils.isEmpty(fastAccount.getSellAdjustRate())) {
                adjustRate = fastAccount.getSellAdjustRate();
            } else {
                adjustRate = StringUtils.isEmpty(exchangeFastCoin.getSellAdjustRate())
                        ? BigDecimal.ZERO
                        : exchangeFastCoin.getSellAdjustRate();
            }
        }
        return adjustRate;
    }

    /**
     * 构建闪兑发起方账户的交易信息
     *
     * @param order 闪兑订单
     * @return
     */
    private WalletExchangeEntity buildInitiatorWalletExchangeEntity(ExchangeFastOrder order) {
        // 处理 收入、支出 的币种和数量
        // 收入、支出 币种
        String incomeSymbol, outcomeSymbol;
        // 收入、支出 数量
        BigDecimal incomeCoinAmount, outcomeCoinAmount;
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            //买入场景： 闪兑基币币种 -> 闪兑币种
            incomeSymbol = order.getCoinSymbol();
            outcomeSymbol = order.getBaseSymbol();
            incomeCoinAmount = order.getTradedAmount();
            outcomeCoinAmount = order.getAmount();
        } else {
            //卖出场景：闪兑币种 -> 闪兑基币币种
            incomeSymbol = order.getBaseSymbol();
            outcomeSymbol = order.getCoinSymbol();
            incomeCoinAmount = order.getTradedAmount();
            outcomeCoinAmount = order.getAmount();
        }

        return this.buildWalletExchangeEntity(order, order.getMemberId(), incomeSymbol, outcomeSymbol, incomeCoinAmount, outcomeCoinAmount);
    }


    /**
     * 构建闪兑发起方账户的交易信息
     *
     * @param order 闪兑订单
     * @return
     */
    private WalletExchangeEntity buildReceiverWalletExchangeEntity(ExchangeFastOrder order) {
        // 处理 收入、支出 的币种和数量
        // 收入、支出 币种
        String incomeSymbol, outcomeSymbol;
        // 收入、支出 数量
        BigDecimal incomeCoinAmount, outcomeCoinAmount;
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            //闪兑用户 买入场景： 闪兑基币币种 -> 闪兑币种(总账的处理与闪兑用户的账是相反的)
            incomeSymbol = order.getBaseSymbol();
            outcomeSymbol = order.getCoinSymbol();
            incomeCoinAmount = order.getAmount();
            outcomeCoinAmount = order.getTradedAmount();
        } else {
            //闪兑用户 卖出场景：闪兑币种 -> 闪兑基币币种(总账的处理与闪兑用户的账是相反的)
            incomeSymbol = order.getCoinSymbol();
            outcomeSymbol = order.getBaseSymbol();
            incomeCoinAmount = order.getAmount();
            outcomeCoinAmount = order.getTradedAmount();
        }

        return this.buildWalletExchangeEntity(order, order.getReceiveId(), incomeSymbol, outcomeSymbol, incomeCoinAmount, outcomeCoinAmount);
    }

    /**
     * 构建兑换交易信息
     *
     * @param order             闪兑订单
     * @param memberId          兑换用户ID
     * @param incomeSymbol      收入币种
     * @param outcomeSymbol     支出币种
     * @param incomeCoinAmount  收入数量，正数
     * @param outcomeCoinAmount 支出数量，正数
     * @return
     */
    private WalletExchangeEntity buildWalletExchangeEntity(ExchangeFastOrder order,
                                                           Long memberId,
                                                           String incomeSymbol,
                                                           String outcomeSymbol,
                                                           BigDecimal incomeCoinAmount,
                                                           BigDecimal outcomeCoinAmount) {
        WalletExchangeEntity exchangeEntity = new WalletExchangeEntity();
        exchangeEntity.setMemberId(memberId);
        exchangeEntity.setType(TransactionType.EXCHANGE_FAST);
        exchangeEntity.setRefId(String.valueOf(order.getOrderId()));

        WalletBaseEntity sourceEntity = new WalletBaseEntity();
        ///sourceEntity.setCoinId(""); //币种ID 可不填写
        sourceEntity.setCoinUnit(incomeSymbol);
        sourceEntity.setTradeBalance(incomeCoinAmount);
        ///sourceEntity.setTradeFrozenBalance(new BigDecimal("0")); //无冻结数量
        ///sourceEntity.setTradeLockBalance(new BigDecimal("0")); //无锁仓数量
        ///sourceEntity.setServiceCharge(new ServiceChargeEntity()); //无手续费
        sourceEntity.setComment("闪兑币种[参考的业务表：exchange_fast_order]");

        exchangeEntity.setSource(sourceEntity);

        WalletBaseEntity targetEntity = new WalletBaseEntity();
        ///targetEntity.setCoinId("");
        targetEntity.setCoinUnit(outcomeSymbol);
        if (BigDecimalUtil.gt0(outcomeCoinAmount)) {
            targetEntity.setTradeBalance(outcomeCoinAmount.negate());
        } else {
            targetEntity.setTradeBalance(outcomeCoinAmount);
        }
        ///targetEntity.setTradeFrozenBalance(new BigDecimal("0"));
        ///targetEntity.setTradeLockBalance(new BigDecimal("0"));
        ///targetEntity.setServiceCharge(new ServiceChargeEntity());
        targetEntity.setComment("闪兑基币[参考的业务表：exchange_fast_order]");

        exchangeEntity.setTarget(targetEntity);

        return exchangeEntity;
    }

    public ExchangeFastOrderServiceImpl getService() {
        return SpringContextUtil.getBean(ExchangeFastOrderServiceImpl.class);
    }
}