package com.spark.bitrade.constants;

/**
 * 常量定义
 *
 * @author yangch
 * @since 2019-06-09 22:48:35
 */
public class LockSlpConstant {
    /**
     * 处理锁仓数据任务消息
     */
    public final static String KAFKA_MSG_BUILD_TASK = "msg_lock_build_task";
    /**
     * 加速释放任务消息
     */
    public final static String KAFKA_MSG_RELEASE_TASK = "msg_lock_release_task";

    /**
     * 更新任务消息
     */
    public final static String KAFKA_MSG_UPDATE_TASK = "msg_lock_update_task";
}
