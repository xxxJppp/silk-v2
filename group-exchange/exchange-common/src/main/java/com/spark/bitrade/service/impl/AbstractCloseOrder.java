package com.spark.bitrade.service.impl;

import com.spark.bitrade.service.ApplyCloseOrderProvider;
import com.spark.bitrade.service.optfor.RedisListService;
import com.spark.bitrade.service.optfor.RedisSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

/**
 *  
 *
 * @author young
 * @time 2019.09.24 09:35
 */
@Slf4j
public abstract class AbstractCloseOrder implements DisposableBean, CommandLineRunner {
    @Autowired
    private RedisListService redisListService;
    @Autowired
    private RedisSetService redisSetService;
    @Autowired
    private ApplyCloseOrderProvider applyCloseOrderProvider;

    private String taskName;
    private boolean runnable = true;

    public AbstractCloseOrder() {
        this.taskName = getTaskName();
    }

    /**
     * @return
     */
    abstract String getTaskName();

    /**
     * 关闭订单
     *
     * @param orderId
     */
    abstract void closeOrder(String orderId);

    /**
     * 获取任务的key
     *
     * @return
     */
    abstract String getTaskKey();

    /**
     * 获取正在进行任务的key
     *
     * @return
     */
    abstract String getUnderwayTaskKey();

    @Override
    public void run(String... strings) throws Exception {
        log.info("init {} >>> 开始 恢复任务............", taskName);
        recoverMarkUnderwayTask();
        log.info("init {} >>> 完成 恢复任务............", taskName);

        //服务启动完后 再初始化，且恢复数据
        log.info("init {} >>> 开始 初始化任务............", taskName);
        Thread thread = new Thread(this::doWork);
        thread.setName(taskName);
        thread.setDaemon(true);
        thread.start();
        log.info("init {} >>> 完成 初始化任务............", taskName);
    }

    @Override
    public void destroy() throws Exception {
        runnable = false;
    }

    /**
     * 任务
     */
    private void doWork() {
        long sleepTime = 500;
        long sleepTimes = 0;

        while (runnable) {
            try {
                // 获取任务
                String taskId = getTasks();
                if (taskId == null) {
                    //阻塞并逐渐增加阻塞时间
                    if (sleepTimes < 60) {
                        sleepTimes++;
                    }
                    log.info("获取任务 >>> 轮空次数={}", sleepTimes);
                    LockSupport.parkNanos(sleepTime * sleepTimes * 1000000);
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
    private String getTasks() {
        return (String) redisListService.lLeftPop(getTaskKey());
    }

    /**
     * 获取所有任务
     *
     * @return
     */
    protected List<Object> listTasks() {
        return redisListService.lRange(getTaskKey(), 0, -1);
    }

    /**
     * 获取任务数量
     *
     * @return
     */
    protected long listTaskSize() {
        return redisListService.lLen(getTaskKey());
    }

    /**
     * 所有正在进行的任务
     *
     * @return
     */
    protected Set<String> listUnderwayTask() {
        return redisSetService.sMembers(getUnderwayTaskKey());
    }

    /**
     * 标记正在进行的任务
     *
     * @param orderId
     */
    private void markUnderwayTask(String orderId) {
        // 任务放到Redis set中
        redisSetService.sAdd(getUnderwayTaskKey(), orderId);
    }

    /**
     * 取消标记正在进行的任务
     *
     * @param orderId
     */
    private void unMarkUnderwayTask(String orderId) {
        // 删除 Redis set中 的任务
        redisSetService.sRemove(getUnderwayTaskKey(), orderId);
    }

    /**
     * 恢复正在进行的任务
     */
    private void recoverMarkUnderwayTask() {
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
    private void executeTask(String orderId) {
        // 缓存正在进行的任务
        this.markUnderwayTask(orderId);

        applyCloseOrderProvider.apply(() -> {
            closeOrder(orderId);

            // 删除正在进行的任务
            unMarkUnderwayTask(orderId);
        });
    }
}
