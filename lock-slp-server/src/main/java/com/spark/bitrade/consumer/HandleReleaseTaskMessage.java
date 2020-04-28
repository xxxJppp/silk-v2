package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.mq.ReleaseTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 消费加速释放任务消息
 *
 * @author yangch
 * @time 2019-06-09 21:38:15
 *  
 */
@Component
@Slf4j
public class HandleReleaseTaskMessage {

    @Qualifier("handleCommunityReleaseTask")
    private TaskMessageConsumer<ReleaseTaskMessage> handleCommunityReleaseTask;
    @Qualifier("handleShareReleaseTask")
    private TaskMessageConsumer<ReleaseTaskMessage> handleShareReleaseTask;
    @Qualifier("handleSunReleaseTask")
    private TaskMessageConsumer<ReleaseTaskMessage> handleSunReleaseTask;

    /**
     * 消费并处理加速释放任务
     *
     * @param record
     */
    @KafkaListener(topics = LockSlpConstant.KAFKA_MSG_RELEASE_TASK, group = "group-handle")
    public void handle(ConsumerRecord<String, String> record) {
        log.info("接收释放任务消息：{}", record);
        /*
         * 1、获取kafka推过来的数据，将消息转为消息体
         * 2、异步调用消息处理
         */
        ReleaseTaskMessage message = JSON.parseObject(record.value(), ReleaseTaskMessage.class);
        if (message != null) {
            switch (message.getType()) {
                case HANDLE_COMMUNITY_RELEASE_TASK:
                    handleCommunityReleaseTask.consume(message);
                    break;
                case HANDLE_SHARE_RELEASE_TASK:
                    handleShareReleaseTask.consume(message);
                    break;
                case HANDLE_SUN_RELEASE_TASK:
                    handleSunReleaseTask.consume(message);
                    break;
                default:
                    log.info("无效的消息：{}", message);
            }
        } else {
            log.info("========================消费加速释放任务消息为空==============================");
        }
    }

    @Autowired
    public void setHandleCommunityReleaseTask(TaskMessageConsumer<ReleaseTaskMessage> handleCommunityReleaseTask) {
        this.handleCommunityReleaseTask = handleCommunityReleaseTask;
    }

    @Autowired
    public void setHandleShareReleaseTask(TaskMessageConsumer<ReleaseTaskMessage> handleShareReleaseTask) {
        this.handleShareReleaseTask = handleShareReleaseTask;
    }

    @Autowired
    public void setHandleSunReleaseTask(TaskMessageConsumer<ReleaseTaskMessage> handleSunReleaseTask) {
        this.handleSunReleaseTask = handleSunReleaseTask;
    }
}
