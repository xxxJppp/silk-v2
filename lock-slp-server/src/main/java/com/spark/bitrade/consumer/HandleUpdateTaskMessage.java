package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.mq.UpdateTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 消费更新任务消息
 *
 * @author yangch
 * @time 2019-06-09 21:38:15
 *  
 */
@Component
@Slf4j
public class HandleUpdateTaskMessage {


    @Autowired
    @Qualifier("handleUpdateTask")
    private TaskMessageConsumer<UpdateTaskMessage> handleUpdateTask;

    /**
     * 消费并处理更新任务
     *
     * @param record
     */
    @KafkaListener(topics = LockSlpConstant.KAFKA_MSG_UPDATE_TASK, group = "group-handle")
    public void handle(ConsumerRecord<String, String> record) {
        log.info("接收更新任务消息：{}", record);

        /**
         * 1、获取kafka推过来的数据，将消息转为消息体
         * 2、异步调用消息处理
         */
        UpdateTaskMessage message = JSON.parseObject(record.value(), UpdateTaskMessage.class);
        if (message != null) {
            switch (message.getType()) {
                case HANDLE_UPDATE_TASK:
                    handleUpdateTask.consume(message);
                    break;
                default:
                    log.info("无效的消息：{}", message);
            }
        } else {
            log.info("========================消费更新任务消息为空==============================");
        }
    }
}
