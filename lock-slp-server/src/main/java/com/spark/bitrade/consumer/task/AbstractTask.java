package com.spark.bitrade.consumer.task;

import com.spark.bitrade.consumer.TaskMessageConsumer;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.mq.TaskMessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 抽象任务（所有的抽象方法不需要指定Transactional事务注解）
 *
 * @param <M> 传入的消息对象类型
 * @param <P> 上一步任务对象类型
 * @param <N> 下一步任务对象类型
 */
@Slf4j
public abstract class AbstractTask<M extends TaskMessage, P, N> implements TaskMessageConsumer<M> {

//    protected KafkaTemplate<String, String> kafkaTemplate;
//
//    @Autowired
//    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }

    protected TaskMessageWrapper taskMessageWrapper;

    @Autowired
    public void setTaskMessageWrapper(TaskMessageWrapper taskMessageWrapper) {
        this.taskMessageWrapper = taskMessageWrapper;
    }

    /**
     * 运行任务（任务入口）
     *
     * @param message 任务消息
     */
    @Override
    public final void consume(M message) {
        // log.info("接收及处理任务消息---开始：{}", message);
        //1、重新装载任务
        P prev = this.convert(message);
        if (prev == null) {
            log.info("任务中断 [ ref_id = {}, prev = null ]", message.getRefId());
            return; // 中断
        }

        List<N> next;
        //2、幂等性校验
        if (check(prev)) {
            //事务处理 3、4、
            next = this.getServiceBean().transactionHandle(prev, message);
        } else {
            // 3-1 已经处理过
            next = processed(prev, message);
        }

        List<TaskMessage> nextTask = null;

        if (next != null) {
            nextTask = next(next, message);
        }

        //6、推送广播消息
        if (nextTask != null) {
            for (TaskMessage taskMessage : nextTask) {
                push(taskMessage.getTopic(), taskMessage.stringify());
            }
        } else {
            log.info("无后续任务 [ ref_id = {} ] ", message.getRefId());
        }

        // log.info("接收及处理任务消息---结束：{}", message);
    }

    /**
     * 事务单元(不用重写)
     *
     * @param prev 前一个任务
     * @return 推荐人任务（下一任务）
     */
    @Transactional(rollbackFor = Exception.class)
    public List<N> transactionHandle(P prev, M msg) {
        //3、处理业务逻辑
        List<N> next = this.execute(prev, msg);
        //4、更新前一任务的状态
        this.update(prev);

        return next;
    }

    protected Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 下发任务
     *
     * @param topic 主题
     * @param data  数据
     */
    protected void push(String topic, String data) {
        log.info("message dispatch -> [ topic = '{}', data = '{}' ]", topic, data);
//        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
//        future.addCallback((v) -> {
//        }, (e) -> {
//            log.error("任务下发失败 topic = {}, data = {}, err = {}", topic, data, e.getMessage());
//            log.error("任务下发失败 exception", e);
//        });
        taskMessageWrapper.dispatch(topic, data, 2);
    }

    /**
     * 0.获取服务对象
     *
     * @return proxy target
     */
    public abstract AbstractTask<M, P, N> getServiceBean();

    /**
     * 1、将消息转换为任务（获取任务的实时数据）
     *
     * @param message 消息
     * @return 返回实时任务
     */
    public abstract P convert(M message);

    /**
     * 2、任务幂等性校验
     *
     * @param prev 任务
     * @return true=未处理，false=已处理
     */
    public abstract boolean check(P prev);

    /**
     * 3、处理业务逻辑
     *
     * @param prev 任务
     * @param msg  消息
     * @return next
     */
    public abstract List<N> execute(P prev, M msg);

    /**
     * 3-1、已经处理过业务
     *
     * @param prev 前一任务
     * @param msg  消息
     * @return next
     */
    public abstract List<N> processed(P prev, M msg);

    /**
     * 4、更新前一个任务的状态
     *
     * @param prev 前一任务
     * @return true=更新成功，false=更新失败
     */
    public abstract boolean update(P prev);

    /**
     * 5、构建推荐人的任务
     *
     * @param next 任务
     * @return 推荐人任务
     */
    public abstract List<TaskMessage> next(List<N> next, M msg);
}
