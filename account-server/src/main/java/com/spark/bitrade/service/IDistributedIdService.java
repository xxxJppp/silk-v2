package com.spark.bitrade.service;

/**
 *  分布式ID服务
 *
 * @author yangch
 * @time 2019.01.22 10:37
 */
public interface IDistributedIdService {
    long generateId();

    String generateStrId();

    String generateStrId(String prefix);
}
