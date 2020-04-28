package com.spark.bitrade.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  
 *
 * @author young
 * @time 2019.09.29 11:40
 */
@Slf4j
@Service
public class ExchangeCheckOrderServiceImpl
        extends AbstractExchangeCheckOrderServiceImpl
        implements CommandLineRunner, DisposableBean {

    @Override
    public void run(String... strings) throws Exception {
        super.run(strings);
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
    }
}
