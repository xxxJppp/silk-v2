package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.CywRedisKeys;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.redis.PalceService;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.service.optfor.RedisKeyService;
import com.spark.bitrade.trans.OrderAggregation;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

/**
 *  撤单服务
 *
 * @author young
 * @time 2019.09.19 14:17
 */
@Slf4j
@Service
public class CywCancelOrderServiceImpl extends AbstractCloseOrder implements CywCancelOrderService, InitializingBean {
    @Autowired
    private RedisHashService redisHashService;
    @Autowired
    private RedisKeyService redisKeyService;
    @Autowired
    private ExchangeCywOrderService exchangeCywOrderService;
    @Autowired
    private CywWalletProvider cywWalletProvider;
    @Autowired
    private TradeDetailService tradeDetailService;
    @Autowired
    private PalceService palceService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    private CywTradingOrderService tradingOrderService;
    @Autowired
    private CywCheckOrderService checkOrderService;

    // 是否激活启动时恢复任务
    @Value("${task.recover.enabled:false}")
    private boolean enabled;

    // 设置启动时恢复任务的最大任务数量限制（任务数量过大，会导致恢复时间非常长）
    @Value("${task.recover.maxSize:1000}")
    private long maxSize;

    @Override
    public ExchangeOrder claimCancelOrder(Long memberId, String orderId) {
        ExchangeCywOrder order = this.queryTradingOrderFromRedis(memberId, orderId);
        if (order != null) {
            //限制重复提交
            ///this.cancelOrderRequestLimit(orderId);

            //提交撤单消息
            kafkaTemplate.send("exchange-order-cancel", order.getSymbol(), JSON.toJSONString(order));
        }

        return order;
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        // 从缓存中获取订单
        ExchangeCywOrder order = this.queryTradingOrderFromRedis(memberId, orderId);
        if (order == null) {
            log.info("交易队列中未获取到订单。orderId={}", orderId);
            return null;
        }

        //更新订单的成交数量、成交额、订单状态、完成时间等
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        order.setStatus(ExchangeOrderStatus.CANCELED);
        order.setCanceledTime(System.currentTimeMillis());

        //快速撤销订单
        return this.getService().fastCanceledOrder(order);
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId) {
        log.warn("撤销的订单不在撮合器中。memberId={}, orderId={}", memberId, orderId);

        // 从缓存中获取订单
        ExchangeCywOrder order = this.queryTradingOrderFromRedis(memberId, orderId);
        if (order == null) {
            log.info("交易队列中未获取到订单。orderId={}", orderId);
            return null;
        }

        //todo 需要确保成交明细都已经保存

        OrderAggregation aggregation = tradeDetailService.aggregation(order);
        log.info(">>> 订单聚合数据 :{}", aggregation);

        //更新订单的成交数量、成交额、订单状态、完成时间等
        order.setTradedAmount(aggregation.getTradedAmount());
        order.setTurnover(aggregation.getTradedTurnover());
        order.setStatus(ExchangeOrderStatus.CANCELED);
        order.setCanceledTime(System.currentTimeMillis());

        //快速撤销订单
        return this.getService().fastCanceledOrder(order);
    }

    /**
     * 快速撤销订单
     *
     * @param order 撤销的订单信息
     * @return
     */
    public ExchangeOrder fastCanceledOrder(ExchangeOrder order) {
        // 删除交易中的订单
        if (this.deleteTradingOrderFromReids(order.getMemberId(), order.getOrderId()) > 0) {
            // 订单存放到撤销队列中
            this.addCanceledOrderToReids(order);

            // 撤单通知
            this.addTask(order.getOrderId());
        }

        return order;
    }

    /**
     * 限制同一订单60秒内只能撤单请求的一次
     *
     * @param orderId
     * @return true=限制访问，false=可以访问
     */
    @Override
    public boolean isCancelOrderRequestLimit(String orderId) {
        return !palceService.place(
                new StringBuilder("lock:req:cancelOrder:").append(orderId).toString(), 60);

//        AssertUtil.isTrue(palceService.place(
//                new StringBuilder("lock:req:cancelOrder:").append(orderId).toString(), 60),
//                CommonMsgCode.FORBID_RESUBMIT);
    }


    /**
     * 入库撤销订单
     * 备注：失败的订单记录到告警表，如记录失败则告警到错误日志中
     *
     * @param orderId 订单号
     */
    @Override
    public void canceledOrder(String orderId) {
        log.info("执行撤单任务，orderId={}", orderId);
        //从撤单队列中获取订单信息
        ExchangeCywOrder order = this.queryCanceledOrderFromRedis(orderId);
        if (order != null) {
            try {
                this.getService().canceledOrder(order);
            } catch (Exception ex) {
                log.warn("撤销订单失败,记录失败订单。订单号=" + orderId, ex);
                try {
                    //记录操作失败的订单
                    businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__CYW_CANCEL_FAIL,
                            order.toString(), ex.getMessage());
                } catch (Exception ex1) {
                    log.error("处理完成的订单失败，需手工处理。订单号=" + orderId, ex1);
                }
            }
        } else {
            log.info("撤单队列中无此订单，可能为重复撤销的订单。订单号={}", orderId);
        }
    }

    @Override
    public void redo(String orderId) {
        log.info("redo >>> 执行撤单任务，orderId={}", orderId);
        //从撤单队列中获取订单信息
        ExchangeCywOrder order = this.queryCanceledOrderFromRedis(orderId);
        if (order != null) {
            this.getService().canceledOrder(order);
        } else {
            log.info("redo >>> 撤单队列中无此订单，可能为重复撤销的订单。订单号={}", orderId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void canceledOrder(ExchangeCywOrder order) {
        WalTradeType tradeType = WalTradeType.CANCEL_ORDER;

        // 有成交额或成交量 就保存订单
        if (BigDecimalUtil.gt0(order.getTradedAmount()) || BigDecimalUtil.gt0(order.getTurnover())) {
            // 保存订单，成交订单入库保存
            AssertUtil.isTrue(exchangeCywOrderService.save(order), ExchangeCywMsgCode.SAVE_CANCELED_ORDER_FAILED);
            tradeType = WalTradeType.PART_CANCEL_ORDER;

            // 添加校验任务
            checkOrderService.addCheckTask(order.getOrderId());
        }

        // 退回多余冻结的余额，记录wal日志
        OrderSettleDelta orderSettleDelta = OrderSettleDelta.settle(order);
        log.info(">>> 订单撤单结算 :{}", orderSettleDelta);
        if (BigDecimalUtil.gt0(orderSettleDelta.getReturnAmount())) {
            log.info(">>> 退还冻结余额,memberId={}, orderId={}, returnAmount={}",
                    orderSettleDelta.getMemberId(), orderSettleDelta.getOrderId(), orderSettleDelta.getReturnAmount());
            cywWalletProvider.giveBackFrozen(orderSettleDelta, tradeType);
        }

        // 从Redis中删除已撤销的订单
        this.deleteCanceledOrderFromReids(order.getSymbol(), order.getOrderId());
    }


    /**
     * 从Redis中获取撤销的订单
     *
     * @param orderId
     * @return
     */
    @Override
    public ExchangeCywOrder queryCanceledOrderFromRedis(String orderId) {
        return (ExchangeCywOrder) redisHashService.hGet(CywRedisKeys.getCywOrderCanceledKey(CywRedisKeys.parseSymbolFromOrderId(orderId)), orderId);
    }

    @Override
    protected String getTaskName() {
        return "daemon-cancel";
    }

    @Override
    protected void closeOrder(String orderId) {
        canceledOrder(orderId);
    }

    @Override
    protected String getTaskKey() {
        return CywRedisKeys.CYW_CANCELED_TASK_KEY;
    }

    @Override
    protected String getUnderwayTaskKey() {
        return CywRedisKeys.CYW_CANCELED_TASKING_KEY;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.recoverTasks();
    }


    public CywCancelOrderServiceImpl getService() {
        return SpringContextUtil.getBean(CywCancelOrderServiceImpl.class);
    }

    /**
     * 订单存放到撤销队列中
     *
     * @param order
     */
    private void addCanceledOrderToReids(ExchangeOrder order) {
        redisHashService.hPut(CywRedisKeys.getCywOrderCanceledKey(order.getSymbol()), order.getOrderId(), order);
    }

    /**
     * 从Redis中删除已撤销的订单
     *
     * @param symbol
     * @param orderId
     * @return 返回删除的数量
     */
    private long deleteCanceledOrderFromReids(String symbol, String orderId) {
        return redisHashService.hDelete(CywRedisKeys.getCywOrderCanceledKey(symbol), orderId);
    }

    private ExchangeCywOrder queryTradingOrderFromRedis(Long memberId, String orderId) {
        return tradingOrderService.queryTradingOrderFromRedis(memberId, orderId);
    }

    /**
     * 从Redis中删除交易中的订单
     *
     * @param memberId
     * @param orderId
     * @return 返回删除的数量
     */
    private long deleteTradingOrderFromReids(Long memberId, String orderId) {
        return tradingOrderService.deleteTradingOrderFromReids(memberId, orderId);
    }

    /**
     * 恢复任务
     */
    private void recoverTasks() {
        if (!enabled) {
            log.info("completed: 已禁用启动时，恢复任务");
            return;
        }

        Set<String> keys = redisKeyService.keys(CywRedisKeys.getCywOrderCanceledKeys());
        log.info("init >>> 恢复已撤单订单任务，keys={}............", keys);
        if (keys.size() > 0) {
            keys.forEach(k -> {
                new Thread(() -> recoverTask(k)).start();
            });
        }
    }

    private void recoverTask(String taskKey) {
        log.info("recoverTask >>> 开始 恢复任务，key={}", taskKey);

        if (redisHashService.hSize(taskKey) > maxSize || listTaskSize() > maxSize * 3) {
            log.info("canceled: 任务量大，不进行恢复，key={}", taskKey);
            return;
        }

        // 获取待处理的任务
        Set<Object> orders = redisHashService.hKeys(taskKey);
        // 获取任务列表
        List<Object> tasks = listTasks();
        // 获取正在进行的任务
        Set<String> ingTasks = listUnderwayTask();
        LockSupport.parkNanos(2000 * 1000000);
        // 恢复任务
        orders.forEach(orderId -> {
            if (!(tasks.contains(orderId) || ingTasks.contains(orderId))) {
                log.info("recover canceled task >>> 恢复{}", orderId);
                addTask(orderId.toString());
            } else {
                log.info("recover canceled task >>> skip{}", orderId);
            }
        });

        tasks.clear();
        ingTasks.clear();
        log.info("recoverTask >>> 完成 恢复任务，key={}", taskKey);
    }

}
