package com.spark.bitrade.service.impl;

import com.spark.bitrade.service.ApplyCloseOrderProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  
 *
 * @author young
 * @time 2019.09.20 19:03
 */
@Slf4j
@Service
public class ApplyCloseOrderProviderImpl implements ApplyCloseOrderProvider, InitializingBean, DisposableBean {

    /**
     * 线程池初始化线程数，默认20
     */
    @Value("${threadPool.closeOrder.corePoolSize:20}")
    int corePoolSize;
    /**
     * 线程池最大线程数，默认200
     */
    @Value("${threadPool.closeOrder.maximumPoolSize:200}")
    int maximumPoolSize;
    private ThreadPoolExecutor executor;

    /**
     * @param task
     * @return
     */
    @Override
    public Future<?> apply(Runnable task) {
        return executor.submit(task);
    }


    @Override
    public void destroy() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("init >>> 开始 初始化连接池............corePoolSize={}, maximumPoolSize={}", corePoolSize, maximumPoolSize);
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        log.info("init >>> 完成 初始化连接池............");
    }
}
