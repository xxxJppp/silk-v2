package com.spark.bitrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/***
  * Async线程池配置
 *
  * @author yangch
  * @time 2018.08.14 13:51
  */

@Configuration
public class SpringAsyncConfig {

    /**
     * 最小的线程数，缺省：1
     */
    @Value("${spring.async.min.size:10}")
    private int corePoolSize;
    /**
     * 最大的线程数，缺省：Integer.MAX_VALUE
     */
    @Value("${spring.async.max.size:0}")
    private int maxPoolSize = 100;
    /**
     * 缺省值为：Integer.MAX_VALUE
     * 当最小的线程数已经被占用满后，新的任务会被放进queue里面，
     * 当这个queue的capacity也被占满之后，pool里面会创建新线程处理这个任务，直到总线程数达到了max size，
     * 这时系统会拒绝这个任务并抛出TaskRejectedException异常（缺省配置的情况下，可以通过rejection-policy来决定如何处理这种情况）
     */
    @Value("${spring.async.queue.capacity:0}")
    private int queueCapacity = 10000;

    @Value("${spring.async.thread.name.prefix:async-}")
    private String threadNamePrefix = "async-";

    /**
     * 线程存活的时间（5分钟）
     */
    @Value("${spring.async.keep.alive.seconds:3000}")
    private int keepAliveSeconds;


    /**
     * 订单状态处理
     */
    @Value("${spring.async.order.min.size:50}")
    private int orderPoolSizeMin;
    @Value("${spring.async.order.max.size:200}")
    private int orderPoolSizeMax;
    @Value("${spring.async.order.queue.capacity:500}")
    private int orderQueueCapacity;


    /**
     * 线程池配置 交易明细
     */
    @Value("${spring.async.trade.min.size:5}")
    private int tradePoolSizeMin;
    @Value("${spring.async.trade.max.size:50}")
    private int tradePoolSizeMax;
    @Value("${spring.async.trade.queue.capacity:200}")
    private int tradeQueueCapacity;

    /**
     * MongoDB库
     */
    @Value("${spring.async.mongodb.min.size:2}")
    private int mongodbPoolSizeMin;
    @Value("${spring.async.mongodb.max.size:100}")
    private int mongodbPoolSizeMax;
    @Value("${spring.async.mongodb.queue.capacity:200}")
    private int mongodbQueueCapacity;

    /**
     * PromoteReward库
     */
    @Value("${spring.async.reword.min.size:1}")
    private int rewordPoolSizeMin;
    @Value("${spring.async.reword.max.size:10}")
    private int rewordPoolSizeMax;
    @Value("${spring.async.reword.queue.capacity:200}")
    private int rewordQueueCapacity;

    /**
     * 初始化线程池
     *
     * @param threadNamePrefix  线程池的前缀名称
     * @param corePoolSize      最小的线程数
     * @param maxPoolSize       最大的线程数
     * @param queueCapacity
     * @param rejectedExecution
     * @return
     */
    public AsyncTaskExecutor taskExecutor(String threadNamePrefix
            , int corePoolSize, int maxPoolSize, int queueCapacity, RejectedExecutionHandler rejectedExecution) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        if (StringUtils.hasText(threadNamePrefix)) {
            executor.setThreadNamePrefix(threadNamePrefix);
        }
        if (corePoolSize > 0) {
            executor.setCorePoolSize(corePoolSize);
        }
        if (maxPoolSize > 0) {
            executor.setMaxPoolSize(maxPoolSize);
        }
        if (queueCapacity > 0) {
            executor.setQueueCapacity(queueCapacity);
        }
        if (keepAliveSeconds > 0) {
            executor.setKeepAliveSeconds(keepAliveSeconds);
        }

        // 设置拒绝策略 rejection-policy：当pool已经达到max size的时候，如何处理新任务
        if (rejectedExecution != null) {
            //不在新线程中执行任务，而是有调用者所在的线程来执行
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        }

        return executor;
    }

    /**
     * 自定义异步线程池
     *
     * @return
     */
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        return taskExecutor(threadNamePrefix, corePoolSize, maxPoolSize
                , queueCapacity, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 处理 交易明细线程池
     *
     * @return
     */
    @Bean("trade")
    public AsyncTaskExecutor taskExecutorProcessExchangeTrade() {
        return taskExecutor("trade-", tradePoolSizeMin, tradePoolSizeMax
                , tradeQueueCapacity, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 处理 订单状态处理
     *
     * @return
     */
    @Bean("order")
    public AsyncTaskExecutor taskExecutorOrder() {
        return taskExecutor("order-", orderPoolSizeMin, orderPoolSizeMax
                , orderQueueCapacity, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 处理 MongoDB库线程池
     *
     * @return
     */
    @Bean("mongodb")
    public AsyncTaskExecutor taskExecutorMongodb() {
        return taskExecutor("mongodb-", mongodbPoolSizeMin, mongodbPoolSizeMax
                , mongodbQueueCapacity, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 处理 reword线程池
     *
     * @return
     */
    @Bean("reword")
    public AsyncTaskExecutor taskExecutorReword() {
        return taskExecutor("reword-", rewordPoolSizeMin, rewordPoolSizeMax
                , rewordQueueCapacity, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
