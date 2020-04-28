package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.ExchangeRedisKeys;
import com.spark.bitrade.lock.Callback;
import com.spark.bitrade.lock.DistributedLockTemplate;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.service.optfor.RedisKeyService;
import com.spark.bitrade.service.optfor.RedisStringService;
import com.spark.bitrade.trans.DiscountRate;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.trans.Tuple2;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.OrderUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.concurrent.locks.LockSupport;

/**
 *  交易明细处理
 *
 * @author young
 * @time 2019.09.03 14:44
 */
@Slf4j
public abstract class AbstractExchangeTradeServiceImpl implements ExchangeTradeService {
    @Resource
    protected OrderFacadeService orderFacadeService;
    @Autowired
    protected ExchangeOrderService exchangeOrderService;
    @Resource
    protected ExchangeCoinService exchangeCoinService;
    @Resource
    protected TradeDetailService tradeDetailService;
    @Autowired
    protected ExchangeWalletProvider exchangeWalletProvider;
    @Autowired
    protected BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    protected RedisKeyService redisKeyService;
    @Autowired
    protected RedisStringService redisStringService;
    @Autowired
    protected RedisHashService redisHashService;
    @Resource
    protected PushMessage pushMessage;
    @Autowired
    protected DiscountService discountService;
    @Autowired
    protected PromoteRewardService promoteRewardService;
    @Autowired
    protected DistributedLockTemplate lockTemplate;

    @Override
    public ExchangeOrder processTrade(ExchangeTrade trade, ExchangeOrderDirection direction) {
        log.info("direction={}, trade = {}", direction.toString(), trade);
        AssertUtil.notNull(trade, ExchangeOrderMsgCode.EXCHANGE_TRADE_IS_NULL);

        ExchangeOrder order = this.queryOrder(trade, direction);

        // 分布式锁，避免并发问题
        return lockTemplate.execute(this.getLockId(trade, order), 10, new Callback<ExchangeOrder>() {
            @Override
            public ExchangeOrder onGetLock() throws Exception {
                // 判断交易明细是否已处理
                if (hasProcessed(trade, order)) {
                    log.info("交易明细已处理，订单号：{}", order.getOrderId());
                    unMarkOrderTrading(order.getOrderId());
                    return order;
                }
                // tips: 解决和非正常的撤单冲突： 标记订单的交易明细正在处理，非正常的撤单不可用
                markOrderTrading(order.getOrderId());

                // 获取交易对配置
                ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol4LocalCache(trade.getSymbol());
                AssertUtil.notNull(exchangeCoin, ExchangeOrderMsgCode.EXCHANGE_COIN_IS_NULL);

                // 获取用户的折扣率
                DiscountRate discountRate = discountService.getDiscountRate(order.getMemberId(), order.getSymbol());

                // 交易结算
                TradeSettleDelta settleDelta = TradeSettleDelta.settle(direction, order, trade, exchangeCoin, discountRate);
                log.info(">>> 交易结算：{}", settleDelta);

                // 保存成交详情
                tradeDetailService.saveTradeDetail(settleDelta);

                // 账户数据处理：分别处理 支出币种、收入币种的账和交易记录
                tradeSettle(settleDelta);

                // 删除
                unMarkOrderTrading(order.getOrderId());

                return order;
            }

            @Override
            public ExchangeOrder onTimeout() throws Exception {
                // 获取锁超时，说明存在并发（该任务只能被成功的执行一次，可以丢掉任务）
                log.warn("处理交易明细存在并发，lockId={}", getLockId(trade, order));
                return order;
            }
        });
    }

    @Override
    @Async("trade")
    public void asyncProcessTrade(ExchangeTrade trade, ExchangeOrderDirection direction) {
        try {
            ExchangeOrder order = processTrade(trade, direction);

            // 推送部分成交消息，仅推送部分成交的订单（交易对中只有一个未成交）
            if (order.getOrderId().equalsIgnoreCase(trade.getUnfinishedOrderId())) {
                order.setTurnover(trade.getUnfinishedTradedTurnover());
                order.setTradedAmount(trade.getUnfinishedTradedAmount());

                pushMessage.pushOrderTrade(order);
            }
        } catch (Exception e) {
            // 容错考虑
            retry(trade, direction, e);
        }

        // 删除任务
        this.unMarkUnderwayTask(trade, direction);
    }

    @Override
    public void retryProcessTrade(ExchangeTrade trade, ExchangeOrderDirection direction) {
        // 主要的错误为 主从不同步或是数据库连接不可用，重试即可解决问题
        log.info("重试,direction={}, trade={}", direction, trade);
        try {
            processTrade(trade, direction);
        } catch (Exception e) {
            // N秒以内，重试
            if (haveATry(trade.getTime())) {
                retry(trade, direction, e);
                // 堵塞，控制频率
                LockSupport.parkNanos(500 * 1000000);
            } else {
                // 处理失败，则记录到告警表
                addWarnRecord(trade, direction, e);
            }
        }
    }


    @Override
    public ExchangeTrade processTrade(ConsumerRecord<String, String> record) {
        log.info("成交明细,key={}, value={}", record.key(), record.value());
        ExchangeTrade trade = JSON.parseObject(record.value(), ExchangeTrade.class);
        return processTrade(record, trade);
    }

    protected ExchangeTrade processTrade(ConsumerRecord<String, String> record, ExchangeTrade trade) {
        if (record.key().equalsIgnoreCase("BUY")) {
            // 记录任务
            this.markUnderwayTask(trade, ExchangeOrderDirection.BUY);
            getService().asyncProcessTrade(trade, ExchangeOrderDirection.BUY);
        } else {
            // 记录任务
            this.markUnderwayTask(trade, ExchangeOrderDirection.SELL);
            getService().asyncProcessTrade(trade, ExchangeOrderDirection.SELL);
        }
        return trade;
    }

    public void run(String... strings) throws Exception {
        log.info("recover data >>> 开始 恢复交易明细任务............");
        this.recoverMarkUnderwayTask(ExchangeOrderDirection.BUY);
        this.recoverMarkUnderwayTask(ExchangeOrderDirection.SELL);
        log.info("recover data >>> 完成 恢复交易明细任务............");
    }

    protected void retry(ExchangeTrade trade, ExchangeOrderDirection direction, Exception e) {
        try {
            // 发送重试消息
            pushMessage.pushTradeRetry(trade, direction);
        } catch (Exception ex) {
            // 发送消息失败，则记录到告警表
            addWarnRecord(trade, direction, e);
        }
    }

    protected void addWarnRecord(ExchangeTrade trade, ExchangeOrderDirection direction, Exception e) {
        try {
            if (direction == ExchangeOrderDirection.BUY) {
                businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__USER_TRADE_BUY, trade.toString(), e.getMessage());
            } else {
                businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__USER_TRADE_SELL, trade.toString(), e.getMessage());
            }
        } catch (Exception ex) {
            log.error("订单明细处理失败，需手工处理，订单类型:{}，撮单信息:{}", ExchangeOrderDirection.BUY.toString(), trade);
            log.error("订单明细处理失败，需手工处理", e);
        }
    }

    /**
     * 是否继续重试
     *
     * @param time
     * @return
     */
    protected boolean haveATry(Long time) {
        // 时间戳转换为秒的比较（重试5秒）
        return (System.currentTimeMillis() - time) / 1000 < 1 * 5;
    }

    /**
     * 查询订单
     *
     * @param orderId
     * @return
     */
    protected ExchangeOrder queryOrder(Long memberId, String orderId) {
        //校验订单号格式
        OrderUtil.checkOrderIdFormat(orderId);
//        /*if (!orderId.startsWith(ExchangeConstants.ORDER_PREFIX)) {
//            ExceptionUitl.throwsMessageCodeException(ExchangeOrderMsgCode.BAD_ORDER);
//        }*/

        return exchangeOrderService.queryOrderWithMaster(memberId, orderId);
        ///return orderFacadeService.queryOrder(memberId, orderId);
    }

    /**
     * 查询订单
     *
     * @param trade
     * @param direction
     * @return
     */
    protected ExchangeOrder queryOrder(ExchangeTrade trade, ExchangeOrderDirection direction) {
        ExchangeOrder order;
        if (ExchangeOrderDirection.BUY == direction) {
            order = this.queryOrder(trade.getBuyMemberId(), trade.getBuyOrderId());
        } else {
            order = this.queryOrder(trade.getSellMemberId(), trade.getSellOrderId());
        }
        AssertUtil.notNull(order, ExchangeOrderMsgCode.EXCHANGE_ORDER_IS_NULL);
        //备注：因生成的订单ID重复，出现过订单信息和撮合信息不一致的情况
        if (!order.getSymbol().equals(trade.getSymbol())) {
            log.error("错误的撮合明细，需手工处理。订单信息={}, 撮合信息={}", order, trade);
            throw ExchangeOrderMsgCode.ERROR_EXCHANGE_TRADE.asException();
        }
        return order;
    }

    /**
     * 是否已处理
     *
     * @param trade
     * @param order
     * @return
     */
    protected boolean hasProcessed(ExchangeTrade trade, ExchangeOrder order) {
        return tradeDetailService.existsByOrderIdAndRefOrderId(order.getOrderId(), this.getRefOrderId(trade, order));
    }

    /**
     * 获取订单的关联订单
     *
     * @param trade
     * @param order
     * @return
     */
    protected String getRefOrderId(ExchangeTrade trade, ExchangeOrder order) {
        if (order.getOrderId().equalsIgnoreCase(trade.getSellOrderId())) {
            return trade.getBuyOrderId();
        }
        return trade.getSellOrderId();
    }

    /**
     * 获取分布式锁ID
     *
     * @param trade
     * @param order
     * @return
     */
    protected String getLockId(ExchangeTrade trade, ExchangeOrder order) {
        return new StringBuilder("lock:")
                .append(order.getOrderId())
                .append("-")
                .append(this.getRefOrderId(trade, order))
                .toString();
    }

    String getUnderwayTaskKey(ExchangeOrderDirection direction) {
        return ExchangeRedisKeys.EX_TRADE_TASKING_KEY + direction.name();
    }

    /**
     * 标记正在进行的任务
     */
    protected void markUnderwayTask(ExchangeTrade trade, ExchangeOrderDirection direction) {
        String key;
        if (direction == ExchangeOrderDirection.BUY) {
            key = trade.getBuyOrderId() + trade.getSellOrderId();
        } else {
            key = trade.getSellOrderId() + trade.getBuyOrderId();
        }

        redisHashService.hPut(getUnderwayTaskKey(direction), key, trade);
    }

    /**
     * 取消标记正在进行的任务
     */
    protected void unMarkUnderwayTask(ExchangeTrade trade, ExchangeOrderDirection direction) {
        String key;
        if (direction == ExchangeOrderDirection.BUY) {
            key = trade.getBuyOrderId() + trade.getSellOrderId();
        } else {
            key = trade.getSellOrderId() + trade.getBuyOrderId();
        }

        redisHashService.hDelete(getUnderwayTaskKey(direction), key);
    }

    /**
     * 恢复正在进行的任务
     */
    protected void recoverMarkUnderwayTask(ExchangeOrderDirection direction) {
        redisHashService.hValues(getUnderwayTaskKey(direction)).forEach(t -> {
            log.info("init >>> 恢复任务，{}", t);
            getService().asyncProcessTrade((ExchangeTrade) t, ExchangeOrderDirection.BUY);
        });
    }

    /**
     * 记录正在进行的订单
     */
    protected void markOrderTrading(String orderId) {
        redisStringService.set(ExchangeRedisKeys.getOrderTradingKey(orderId), System.currentTimeMillis());
    }

    /**
     * 删除正在进行的订单
     */
    protected void unMarkOrderTrading(String orderId) {
        redisKeyService.delete(ExchangeRedisKeys.getOrderTradingKey(orderId));
    }

    /**
     * 交易结算处理
     *
     * @param settleDelta
     */
    protected void tradeSettle(TradeSettleDelta settleDelta) {
        Tuple2<ExchangeWalletWalRecord, ExchangeWalletWalRecord> tuple2;
        try {
            tuple2 = exchangeWalletProvider.tradeSettle(settleDelta);
        } catch (Exception ex) {
            log.error("交易结算失败", ex);

            try {
                log.warn("tips：交易结算失败，删除交易明细，{}", settleDelta);
                tradeDetailService.deleteTradeDetail(settleDelta);
            } catch (Exception ex1) {
                log.error("删除交易明细记录失败，需手工处理。{}", settleDelta);
            }

            throw ExchangeOrderMsgCode.ERROR_TRADE_SETTLE.asException();
        }

        // 实时返佣（异步）
        try {
            if (tuple2 != null) {
                promoteRewardService.reword(tuple2.getFirst());
            }
        } catch (Exception ex) {
            log.error("返佣失败", ex);
        }
    }


    public AbstractExchangeTradeServiceImpl getService() {
        return SpringContextUtil.getBean(AbstractExchangeTradeServiceImpl.class);
    }

}
