package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.mq.BuildTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 处理锁仓数据任务消息
 *
 * @author yangch
 * @time 2019-06-09 21:38:15
 *  
 */
@Component
@Slf4j
public class HandleBuildTaskMessage {

    @Autowired
    @Qualifier("handleLockSlpTask")
    private TaskMessageConsumer<BuildTaskMessage> handleLockSlpTask;

    @Autowired
    @Qualifier("handleLockSlpPreAppendTask")
    private TaskMessageConsumer<BuildTaskMessage> handleLockSlpPreAppendTask;

    @Autowired
    @Qualifier("handleLockSlpAppendTask")
    private TaskMessageConsumer<BuildTaskMessage> handleLockSlpAppendTask;

    @Autowired
    @Qualifier("buildActorUpdateTask")
    private TaskMessageConsumer<BuildTaskMessage> buildActorUpdateTask;

    @Autowired
    @Qualifier("buildInviterUpdateTask")
    private TaskMessageConsumer<BuildTaskMessage> buildInviterUpdateTask;

    @Autowired
    @Qualifier("buildInviterReleaseTask")
    private TaskMessageConsumer<BuildTaskMessage> buildInviterReleaseTask;

    /**
     * 消费并处理锁仓数据任务
     *
     * @param record
     */
    @KafkaListener(topics = LockSlpConstant.KAFKA_MSG_BUILD_TASK, group = "group-handle")
    public void handle(ConsumerRecord<String, String> record) {
        log.info("接收构建任务消息：{}", record);
        /**
         * 1、获取kafka推过来的数据，将消息转为消息体
         * 2、异步调用消息处理
         */
        BuildTaskMessage message = JSON.parseObject(record.value(), BuildTaskMessage.class);
        if (message != null) {
            switch (message.getType()) {
                case LOCK_SLP_TASK:
                    handleLockSlpTask.consume(message);
                    break;
                case BUILD_ACTOR_UPDATE_TASK:
                    buildActorUpdateTask.consume(message);
                    break;
                case BUILD_INVITER_UPDATE_TASK:
                    buildInviterUpdateTask.consume(message);
                    break;
                case BUILD_INVITER_RELEASE_TASK:
                    buildInviterReleaseTask.consume(message);
                    break;
                case LOCK_SLP_PRE_APPEND_TASK:
                    handleLockSlpPreAppendTask.consume(message);
                    break;
                case LOCK_SLP_POST_APPEND_TASK:
                    handleLockSlpAppendTask.consume(message);
                    break;
                default:
                    log.info("无效的消息：{}", message);
            }
        } else {
            log.info("========================消费并处理锁仓数据任务为空==============================");
        }
    }

}
