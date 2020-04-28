package com.spark.bitrade.service;

import java.util.concurrent.Future;

/**
 *  
 *
 * @author young
 * @time 2019.09.20 19:09
 */
public interface ApplyCloseOrderProvider {

    Future<?> apply(Runnable task);
}
