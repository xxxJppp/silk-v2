package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeReleaseConstants;
import com.spark.bitrade.service.ExchangeReleaseReferrerOrderService;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *  奖励任务kafka消费
 *
 * @author young
 * @time 2019.09.17 14:02
 */
@Slf4j
@Component
public class AwardTaskConsumer {
    @Autowired
    private ExchangeReleaseReferrerOrderService referrerOrderService;

    /**
     * 重试
     */
    @KafkaListener(topics = ExchangeReleaseConstants.TOPIC_AWARD_TASK, group = "group-handle")
    public void handleAwardTask(ConsumerRecord<String, String> record) {
        getService().AwardTask(record);
    }

    @Async
    void AwardTask(ConsumerRecord<String, String> record) {
        referrerOrderService.exchange(JSON.parseObject(record.value(), String.class));
    }

    public AwardTaskConsumer getService() {
        return SpringContextUtil.getBean(AwardTaskConsumer.class);
    }
}
