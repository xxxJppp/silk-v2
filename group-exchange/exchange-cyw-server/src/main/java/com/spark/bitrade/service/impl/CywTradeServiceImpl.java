package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.CywRedisKeys;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.trans.DiscountRate;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.locks.LockSupport;

/**
 *  交易明细处理
 *
 * @author young
 * @time 2019.09.03 14:44
 */
@Slf4j
@Service
public class CywTradeServiceImpl implements CywTradeService, CommandLineRunner {
    @Resource
    private CywOrderService orderService;
    @Resource
    private ExchangeCoinService exchangeCoinService;
    @Resource
    private TradeDetailService tradeDetailService;
    @Autowired
    private CywWalletProvider cywWalletProvider;
    @Autowired
    private BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    private RedisHashService redisHashService;
    @Autowired
    private DiscountService discountService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ExchangeOrder processTrade(ExchangeTrade trade, ExchangeOrderDirection direction) {
        log.info("direction={}, trade = {}", direction.toString(), trade);
        AssertUtil.notNull(trade, ExchangeCywMsgCode.EXCHANGE_TRADE_IS_NULL);

        ExchangeOrder order = this.queryOrder(trade, direction);

        //判断交易明细是否已处理
        if (this.hasProcessed(trade, order)) {
            log.info("交易明细已处理，订单号：{}", order.getOrderId());
            return order;
        }
        // 标记订单正在处理，供“异常撤单”调用(替代解决方案：1、此处进行重试 2、对成交和撤单的订单进行校验)

        //获取交易对配置
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol4LocalCache(trade.getSymbol());
        AssertUtil.notNull(exchangeCoin, ExchangeCywMsgCode.EXCHANGE_COIN_IS_NULL);

        //todo 获取用户的折扣率（自己机器人不用手续费，如对外则需要考虑收手续费）
        ///DiscountRate discountRate = discountService.getDiscountRate(order.getMemberId(), order.getSymbol());
        DiscountRate discountRate = DiscountRate.getNoProcedureFee();

        //交易结算
        TradeSettleDelta settleDelta = TradeSettleDelta.settle(direction, order, trade, exchangeCoin, discountRate);
        log.info(">>> 交易结算：{}", settleDelta);

        //tip:机器人不支持返佣、超级合伙人业务

        //保存成交详情
        tradeDetailService.saveTradeDetail(settleDelta);

        //账户数据处理：分别处理 支出币种、收入币种的账和交易记录
        this.tradeSettle(settleDelta);

        return order;
    }

    @Override
    @Async("trade")
    public void asyncProcessTrade(ExchangeTrade trade, ExchangeOrderDirection direction) {
        try {
            processTrade(trade, direction);
        } catch (Exception e) {
            // 容错考虑
            retry(trade, direction, e);
        }

        // 删除任务
        this.umMarkUnderwayTask(trade, direction);
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
                LockSupport.parkNanos(200 * 1000000);
            } else {
                // 处理失败，则记录到告警表
                addWarnRecord(trade, direction, e);
            }
        }
    }


    @Override
    public void processTrade(ConsumerRecord<String, String> record) {
        log.info("成交明细,key={}, value={}", record.key(), record.value());
        ExchangeTrade trade = JSON.parseObject(record.value(), ExchangeTrade.class);
        if (record.key().equalsIgnoreCase("BUY")) {
            // 记录任务
            this.markUnderwayTask(trade, ExchangeOrderDirection.BUY);
            getService().asyncProcessTrade(trade, ExchangeOrderDirection.BUY);
        } else {
            // 记录任务
            this.markUnderwayTask(trade, ExchangeOrderDirection.SELL);
            getService().asyncProcessTrade(trade, ExchangeOrderDirection.SELL);
        }
    }

    private void retry(ExchangeTrade trade, ExchangeOrderDirection direction, Exception e) {
        try {
            // 发送重试消息
            kafkaTemplate.send("exchange-cyw-retry", direction.name(), JSON.toJSONString(trade));
        } catch (Exception ex) {
            // 发送消息失败，则记录到告警表
            addWarnRecord(trade, direction, e);
        }
    }

    private void addWarnRecord(ExchangeTrade trade, ExchangeOrderDirection direction, Exception e) {
        try {
            if (direction == ExchangeOrderDirection.BUY) {
                businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__CYW_TRADE_BUY, trade.toString(), e.getMessage());
            } else {
                businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__CYW_TRADE_SELL, trade.toString(), e.getMessage());
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
    private boolean haveATry(Long time) {
        //时间戳转换为秒的比较（重试1分钟）
        return (System.currentTimeMillis() - time) / 1000 < 1 * 60;
    }

    /**
     * 查询订单
     *
     * @param orderId
     * @return
     */
    private ExchangeOrder queryOrder(Long memberId, String orderId) {
        //校验订单号格式
        if (!orderId.startsWith("S")) {
            ExceptionUitl.throwsMessageCodeException(ExchangeCywMsgCode.BAD_CYW_ORDER);
        }
        if (!orderId.contains("_")) {
            ExceptionUitl.throwsMessageCodeException(ExchangeCywMsgCode.BAD_CYW_ORDER);
        }

        return orderService.queryOrder(memberId, orderId);
    }

    /**
     * 查询订单
     *
     * @param trade
     * @param direction
     * @return
     */
    private ExchangeOrder queryOrder(ExchangeTrade trade, ExchangeOrderDirection direction) {
        ExchangeOrder order;
        if (ExchangeOrderDirection.BUY == direction) {
            order = this.queryOrder(trade.getBuyMemberId(), trade.getBuyOrderId());
        } else {
            order = this.queryOrder(trade.getSellMemberId(), trade.getSellOrderId());
        }
        AssertUtil.notNull(order, ExchangeCywMsgCode.EXCHANGE_ORDER_IS_NULL);
        //备注：因生成的订单ID重复，出现过订单信息和撮合信息不一致的情况
        if (!order.getSymbol().equals(trade.getSymbol())) {
            log.error("错误的撮合明细，需手工处理。订单信息={}, 撮合信息={}", order, trade);
            throw ExchangeCywMsgCode.ERROR_EXCHANGE_TRADE.asException();
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
    private boolean hasProcessed(ExchangeTrade trade, ExchangeOrder order) {
        //关联订单号
        String refOrderId = trade.getSellOrderId();
        if (order.getOrderId().equalsIgnoreCase(refOrderId)) {
            refOrderId = trade.getBuyOrderId();
        }

        return tradeDetailService.existsByOrderIdAndRefOrderId(order.getOrderId(), refOrderId);
    }

    /**
     * 删除交易明细
     *
     * @param delta
     */
    private void deleteTradeDetail(TradeSettleDelta delta) {
        tradeDetailService.deleteByOrderIdAndRefOrderId(delta.getOrderId(), delta.getRefOrderId());
    }

    String getUnderwayTaskKey(ExchangeOrderDirection direction) {
        return CywRedisKeys.CYW_TRADE_TASKING_KEY + direction.name();
    }

    /**
     * 标记正在进行的任务
     */
    private void markUnderwayTask(ExchangeTrade trade, ExchangeOrderDirection direction) {
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
    private void umMarkUnderwayTask(ExchangeTrade trade, ExchangeOrderDirection direction) {
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
    private void recoverMarkUnderwayTask(ExchangeOrderDirection direction) {
        redisHashService.hValues(getUnderwayTaskKey(direction)).forEach(t -> {
            log.info("init >>> 恢复任务，{}", t);
            getService().asyncProcessTrade((ExchangeTrade) t, ExchangeOrderDirection.BUY);
        });
    }

    /**
     * 交易结算处理
     *
     * @param settleDelta
     */
    private void tradeSettle(TradeSettleDelta settleDelta) {
        try {
            cywWalletProvider.tradeSettle(settleDelta);
        } catch (Exception ex) {
            log.error("交易结算失败", ex);

            try {
                log.warn("tips：交易结算失败，删除交易明细，{}", settleDelta);
                this.deleteTradeDetail(settleDelta);
            } catch (Exception ex1) {
                log.error("删除交易明细记录失败，需手工处理。{}", settleDelta);
            }

            throw ExchangeCywMsgCode.ERROR_TRADE_SETTLE.asException();
        }
    }

    public CywTradeServiceImpl getService() {
        return SpringContextUtil.getBean(CywTradeServiceImpl.class);
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("recover data >>> 开始 恢复交易明细任务............");
        this.recoverMarkUnderwayTask(ExchangeOrderDirection.BUY);
        this.recoverMarkUnderwayTask(ExchangeOrderDirection.SELL);
        log.info("recover data >>> 完成 恢复交易明细任务............");
    }
}
