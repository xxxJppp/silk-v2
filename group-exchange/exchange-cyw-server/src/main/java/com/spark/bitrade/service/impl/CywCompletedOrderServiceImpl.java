package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.CywRedisKeys;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.service.optfor.RedisKeyService;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

/**
 *  
 *
 * @author young
 * @time 2019.09.19 19:00
 */
@Slf4j
@Service
public class CywCompletedOrderServiceImpl extends AbstractCloseOrder implements CywCompletedOrderService, InitializingBean {
    @Autowired
    private RedisHashService redisHashService;
    @Autowired
    private RedisKeyService redisKeyService;
    @Autowired
    private ExchangeCywOrderService exchangeCywOrderService;
    @Autowired
    private CywWalletProvider cywWalletProvider;
    @Autowired
    private BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    private CywTradingOrderService tradingOrderService;
    @Autowired
    private CywCheckOrderService checkOrderService;

    //是否激活启动时恢复任务
    @Value("${task.recover.enabled:false}")
    private boolean enabled;

    // 设置启动时恢复任务的最大任务数量限制（任务数量过大，会导致恢复时间非常长）
    @Value("${task.recover.maxSize:1000}")
    private long maxSize;


    @Override
    public ExchangeOrder completedOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        // 从缓存中获取订单
        ExchangeCywOrder order = tradingOrderService.queryTradingOrderFromRedis(memberId, orderId);
        if (order == null) {
            log.info("交易队列中未获取到订单。orderId={}", orderId);
            return null;
        }

        // 更新订单的成交数量、成交额、订单状态、完成时间等
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        order.setStatus(ExchangeOrderStatus.COMPLETED);
        order.setCompletedTime(System.currentTimeMillis());

        //快速完成订单
        return this.getService().fastCompletedOrder(order);
    }

    /**
     * 从Redis中获取已完成的订单
     *
     * @param orderId
     * @return
     */
    @Override
    public ExchangeCywOrder queryCompletedOrderFromRedis(String orderId) {
        return (ExchangeCywOrder) redisHashService.hGet(CywRedisKeys.getCywOrderCompletedKey(CywRedisKeys.parseSymbolFromOrderId(orderId)), orderId);
    }

    /**
     * 快速完成订单
     *
     * @param order 订单信息
     * @return
     */
    public ExchangeOrder fastCompletedOrder(ExchangeOrder order) {
        // 删除交易中的订单
        if (this.tradingOrderService.deleteTradingOrderFromReids(order.getMemberId(), order.getOrderId()) > 0) {
            // 订单存放到已完成队列中
            this.addCompletedOrderToReids(order);

            // 订单入库通知
            this.addTask(order.getOrderId());
        }

        return order;
    }

    /**
     * 入库已完成的订单
     * 备注：失败的订单记录到告警表，如记录失败则告警到错误日志中
     *
     * @param orderId 订单号
     */
    @Override
    public void completedOrder(String orderId) {
        log.info("执行已完成的订单入库任务，orderId={}", orderId);
        //从已完成队列中获取订单信息
        ExchangeCywOrder order = this.queryCompletedOrderFromRedis(orderId);
        if (order != null) {
            try {
                this.getService().completedOrder(order);
            } catch (Exception ex) {
                log.warn("已完成的订单入库失败,记录失败订单。订单号=" + orderId, ex);
                try {
                    //记录操作失败的订单
                    businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__CYW_COMPLETED,
                            order.toString(), ex.getMessage());
                } catch (Exception ex1) {
                    log.error("处理已完成的订单入库失败，需手工处理。订单号=" + orderId, ex1);
                }
            }
        } else {
            log.info("已完成订单队列中无此订单，可能为重复操作。订单号={}", orderId);
        }
    }

    @Override
    public void redo(String orderId) {
        log.info("redo >>> 执行已完成的订单入库任务，orderId={}", orderId);
        //从已完成队列中获取订单信息
        ExchangeCywOrder order = this.queryCompletedOrderFromRedis(orderId);
        if (order != null) {
            this.getService().completedOrder(order);
        } else {
            log.info("redo >>> 已完成订单队列中无此订单，可能为重复操作。订单号={}", orderId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ExchangeOrder completedOrder(ExchangeCywOrder order) {
        // 保存订单，成交订单入库保存
        AssertUtil.isTrue(exchangeCywOrderService.save(order), ExchangeCywMsgCode.SAVE_COMPLETED_ORDER_FAILED);

        // 退回多余冻结的余额，记录wal日志
        OrderSettleDelta orderSettleDelta = OrderSettleDelta.settle(order);
        log.info(">>> 订单完成结算 :{}", orderSettleDelta);
        if (BigDecimalUtil.gt0(orderSettleDelta.getReturnAmount())) {
            log.info(">>> 退还多冻结的余额,memberId={}, orderId={}, returnAmount={}",
                    orderSettleDelta.getMemberId(), orderSettleDelta.getOrderId(), orderSettleDelta.getReturnAmount());
            cywWalletProvider.giveBackFrozen(orderSettleDelta, WalTradeType.DEAL);
        }

        // 添加校验任务
        checkOrderService.addCheckTask(order.getOrderId());

        // 删除已完成订单队列中的订单
        this.deleteCompletedOrderFromReids(order.getSymbol(), order.getOrderId());

        return order;
    }

    @Override
    protected String getTaskName() {
        return "daemon-completed";
    }

    @Override
    protected void closeOrder(String orderId) {
        completedOrder(orderId);
    }

    @Override
    protected String getTaskKey() {
        return CywRedisKeys.CYW_COMPLETED_TASK_KEY;
    }

    @Override
    protected String getUnderwayTaskKey() {
        return CywRedisKeys.CYW_COMPLETED_TASKING_KEY;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.recoverTasks();
    }


    /**
     * 订单存放到已完成队列中
     *
     * @param order
     */
    private void addCompletedOrderToReids(ExchangeOrder order) {
        redisHashService.hPut(CywRedisKeys.getCywOrderCompletedKey(order.getSymbol()), order.getOrderId(), order);
    }

    /**
     * 从Redis中删除已完成的订单
     *
     * @param symbol
     * @param orderId
     * @return 返回删除的数量
     */
    private long deleteCompletedOrderFromReids(String symbol, String orderId) {
        return redisHashService.hDelete(CywRedisKeys.getCywOrderCompletedKey(symbol), orderId);
    }

    /**
     * 恢复任务
     */
    private void recoverTasks() {
        if (!enabled) {
            log.info("completed: 已禁用启动时，恢复任务");
            return;
        }

        Set<String> keys = redisKeyService.keys(CywRedisKeys.getCywOrderCompletedKeys());
        log.info("init >>> 恢复已完成订单任务，keys={}............", keys);
        if (keys.size() > 0) {
            keys.forEach(k -> {
                new Thread(() -> recoverTask(k)).start();
            });
        }
    }

    /**
     * 恢复任务
     *
     * @param taskKey
     */
    private void recoverTask(String taskKey) {
        log.info("recoverTask >>> 开始 恢复任务，key={}", taskKey);

        if (redisHashService.hSize(taskKey) > maxSize || listTaskSize() > maxSize * 3) {
            log.info("completed:任务量大，不进行恢复，key={}", taskKey);
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
                log.info("recover completed task >>> 恢复{}", orderId);
                addTask(orderId.toString());
            } else {
                log.info("recover completed task >>> skip{}", orderId);
            }
        });

        tasks.clear();
        ingTasks.clear();
        log.info("recoverTask >>> 完成 恢复任务，key={}", taskKey);
    }

    public CywCompletedOrderServiceImpl getService() {
        return SpringContextUtil.getBean(CywCompletedOrderServiceImpl.class);
    }

}
