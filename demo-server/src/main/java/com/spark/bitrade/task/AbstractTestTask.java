package com.spark.bitrade.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 抽象任务（所有的抽象方法不需要指定Transactional事务注解）
 *
 * @param <M> 传入的消息对象类型
 * @param <T> 任务对象类型
 */
@Slf4j
public abstract class AbstractTestTask<M, T> {
    /**
     * 运行任务（任务入口）
     *
     * @param message 任务消息
     */
    public final void executeTask(M message) {
        T nextTask = null;

        //1、重新装载任务
        T task = this.converTask(message);
        //2、幂等性校验
        if (checkTask(task)) {
            //事务处理 3、4、5
            nextTask = this.getServiceBean().transactionHandle(task);
        }

        //6、推送广播消息
        if (nextTask != null) {
            this.pushMessage(nextTask);
        } else {
            log.info("广播的任务不存在");
        }
    }

    /**
     * 事务单元(不用重写)
     *
     * @param task 任务
     * @return 推荐人任务（下一任务）
     */
    @Transactional(rollbackFor = Exception.class)
    public T transactionHandle(T task) {
        //3、处理业务逻辑
        this.executeBusiness(task);
        //4、更新当前任务的状态
        this.updateTaskStatus(task);
        //5、构建推荐人的任务
        return this.builderNextTask(task);
    }

    /**
     * 0.获取服务对象
     *
     * @return
     */
    abstract AbstractTestTask<M, T> getServiceBean();

    /**
     * 1、将消息转换为任务（获取任务的实时数据）
     *
     * @param message 消息
     * @return 返回实时任务
     */
    abstract T converTask(M message);

    /**
     * 2、任务幂等性校验
     *
     * @param task 任务
     * @return true=未处理，false=已处理
     */
    abstract boolean checkTask(T task);

    /**
     * 3、处理业务逻辑
     *
     * @param task 任务
     */
    abstract void executeBusiness(T task);

    /**
     * 4、更新当前任务的状态
     *
     * @param task
     * @return true=更新成功，false=更新失败
     */
    abstract boolean updateTaskStatus(T task);

    /**
     * 5、构建推荐人的任务
     *
     * @param task 任务
     * @return 推荐人任务
     */
    abstract T builderNextTask(T task);

    /**
     * 6、推送广播消息
     *
     * @param nextTask
     * @return true=推送成功，false=推送失败
     */
    abstract boolean pushMessage(T nextTask);
}
