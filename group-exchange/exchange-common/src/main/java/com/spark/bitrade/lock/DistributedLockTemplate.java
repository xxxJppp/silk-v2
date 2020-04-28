package com.spark.bitrade.lock;

/**
 * 分布式锁模板类
 *
 * @author yangch
 * @time 2019.01.15 18:10
 */
public interface DistributedLockTemplate {

    /**
     * 获取分布式锁后执行方法
     *
     * @param lockId   锁id(对应业务唯一ID)
     * @param timeout  单位毫秒
     * @param callback 回调函数
     * @param <T>      回调函数的返回类型
     * @return T
     */
    <T> T execute(String lockId, int timeout, Callback<T> callback);
}
