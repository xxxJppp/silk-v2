package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.constants.ExchangeRedisKeys;
import com.spark.bitrade.service.ExchangeCheckOrderService;
import com.spark.bitrade.service.optfor.RedisListService;
import com.spark.bitrade.service.optfor.RedisSetService;
import com.spark.bitrade.service.optfor.RedisZSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 *  
 *
 * @author young
 * @time 2019.09.29 11:40
 */
@Slf4j
public abstract class AbstractExchangeCheckOrderServiceImpl implements ExchangeCheckOrderService {
    @Autowired
    protected RedisZSetService redisZSetService;
    @Autowired
    protected RedisListService redisListService;
    @Autowired
    protected RedisSetService redisSetService;
    @Autowired
    protected AbstractExchangeOrderValidatorImpl validator;

    protected volatile boolean runnable = true;

    protected String taskName = "daemon-check";

    /**
     * 线程池初始化线程数，默认2
     */
    @Value("${threadPool.checkOrder.corePoolSize:2}")
    int corePoolSize;
    /**
     * 线程池最大线程数，默认10
     */
    @Value("${threadPool.checkOrder.maximumPoolSize:10}")
    int maximumPoolSize;
    protected ThreadPoolExecutor executor;

    /**
     * 检查订单
     */
    public void checkOrder(String orderId) {
        log.info("check >>> 订单校验，订单号={}", orderId);
        try {
            validator.validate(orderId);
        } catch (Exception ex) {
            log.error("订单校验出错", ex);
        }
    }

    @Override
    public boolean addCheckTask(String orderId) {
        return redisZSetService.zAdd(getDelayTaskKey(), orderId, System.currentTimeMillis());
    }


    /**
     * 获取延迟任务的key
     *
     * @return
     */
    protected String getDelayTaskKey() {
        return ExchangeRedisKeys.EX_CHECK_ORDER_DELAY_TASK_KEY;
    }

    /**
     * 获取任务的key
     *
     * @return
     */
    protected String getTaskKey() {
        return ExchangeRedisKeys.EX_CHECK_ORDER_TASK_KEY;
    }

    /**
     * 获取正在进行任务的key
     *
     * @return
     */
    protected String getUnderwayTaskKey() {
        return ExchangeRedisKeys.EX_CHECK_ORDER_ING_TASK_KEY;
    }


    /**
     * 检查是否还有任务
     *
     * @return
     */
    protected boolean hasCheckTasks() {
        double delayTaskTimes = getDelayTaskTimes();
        Set<Object> set = redisZSetService.zReverseRangeByScore(getDelayTaskKey(), 0, delayTaskTimes);
        if (set.size() > 0) {
            redisZSetService.zRemoveRangeByScore(getDelayTaskKey(), 0, delayTaskTimes);
            set.forEach(o -> {
                // 将延迟任务添加到检查任务队列
                this.addTask(o.toString());
            });
        }

        return false;
    }

    protected double getDelayTaskTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -60);
        return calendar.getTimeInMillis();
    }

    public void run(String... strings) throws Exception {
        log.info("init >>> 初始化连接池............corePoolSize={}, maximumPoolSize={}", corePoolSize, maximumPoolSize);
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        log.info("init {} >>> 恢复任务............", taskName);
        recoverMarkUnderwayTask();

        //服务启动完后 再初始化，且恢复数据
        log.info("init {} >>> 初始化任务............", taskName);
        Thread thread = new Thread(this::doWork);
        thread.setName(taskName);
        thread.setDaemon(true);
        thread.start();
    }

    public void destroy() throws Exception {
        runnable = false;
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * 任务
     */
    protected void doWork() {
        long sleepTime = 500;
        long sleepTimes = 0;

        while (runnable) {
            try {
                // 获取任务
                String taskId = getTasks();
                if (taskId == null) {
                    // 获取延迟的任务
                    if (!hasCheckTasks()) {
                        //阻塞并逐渐增加阻塞时间
                        if (sleepTimes < 60) {
                            sleepTimes++;
                        }
                        log.info("获取任务 >>> 轮空次数={}", sleepTimes);
                        LockSupport.parkNanos(sleepTime * sleepTimes * 1000000);
                    }

                    continue;
                }
                if (sleepTimes > 0) {
                    sleepTimes = 0;
                }
                log.info("获得任务 >>> taskId={}", taskId);

                // 提交并执行任务
                executeTask(taskId);
            } catch (Exception ex) {
                log.error("任务执行失败", ex);
            }
        }
    }

    /**
     * 添加任务
     *
     * @param orderId
     */
    protected void addTask(String orderId) {
        redisListService.lRightPush(getTaskKey(), orderId);
    }

    /**
     * 获取撤单任务
     *
     * @return
     */
    protected String getTasks() {
        return (String) redisListService.lLeftPop(getTaskKey());
    }

    /**
     * 标记正在进行的任务
     *
     * @param orderId
     */
    protected void markUnderwayTask(String orderId) {
        // 任务放到Redis set中
        redisSetService.sAdd(getUnderwayTaskKey(), orderId);
    }

    /**
     * 取消标记正在进行的任务
     *
     * @param orderId
     */
    protected void unMarkUnderwayTask(String orderId) {
        // 删除 Redis set中 的任务
        redisSetService.sRemove(getUnderwayTaskKey(), orderId);
    }

    /**
     * 恢复正在进行的任务
     */
    protected void recoverMarkUnderwayTask() {
        // 获取正在进行的列表
        Set<String> recoverTasks = redisSetService.sMembers(getUnderwayTaskKey());
        if (recoverTasks != null) {
            // 重新放到任务列表
            recoverTasks.forEach(o -> addTask(o));
        }
    }

    /**
     * 提交并执行任务
     *
     * @param orderId 订单ID
     */
    protected void executeTask(String orderId) {
        // 缓存正在进行的任务
        this.markUnderwayTask(orderId);

        executor.submit(() -> {
            checkOrder(orderId);

            // 删除正在进行的任务
            unMarkUnderwayTask(orderId);
        });
    }


}
