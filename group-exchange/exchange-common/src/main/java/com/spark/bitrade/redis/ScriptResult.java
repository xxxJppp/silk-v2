package com.spark.bitrade.redis;

/**
 * ScriptResult
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/5 15:50
 */
public interface ScriptResult<T> {

    /**
     * 是否成功
     *
     * @return bool
     */
    boolean isSuccess();

    /**
     * 执行结果
     *
     * @return result
     */
    T getResult();
}
