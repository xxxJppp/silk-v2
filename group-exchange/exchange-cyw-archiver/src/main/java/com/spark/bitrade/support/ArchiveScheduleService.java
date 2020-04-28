package com.spark.bitrade.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ArchiveScheduleService
 *
 * @author Pikachu
 * @since 2019/11/4 11:12
 */
@Slf4j
public class ArchiveScheduleService implements InitializingBean, DisposableBean {

    private ThreadPoolTaskScheduler scheduler;

    private String threadNamePrefix = "archive - ";
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
    private int poolSize = 30;

    public void schedule(Runnable task) {
        scheduler.execute(task);
    }

    public void schedule(ArchiveTask at) {
        scheduler.schedule(at::execute, at);
    }

    @Override
    public void destroy() throws Exception {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (scheduler == null) {
            scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix(threadNamePrefix);
            scheduler.setRejectedExecutionHandler(rejectedExecutionHandler);
            scheduler.setPoolSize(poolSize);

            scheduler.initialize();
        }
    }

    public void setScheduler(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
