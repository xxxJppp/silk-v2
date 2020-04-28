package com.spark.bitrade.lock;

/**
 * 获取到分布式锁后的执行接口
 *
 * @param <T> 接口返回类型
 * @author yangch
 * @since 2019.01.15 18:17
 */
public interface Callback<T> {

    T onGetLock() throws Exception;

    T onTimeout() throws Exception;
}
