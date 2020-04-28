package com.spark.bitrade.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * ReleaseRecordWrapper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/17 19:31
 */
@Slf4j
@Component
public class TaskMessageWrapper implements InitializingBean, DisposableBean {

    private static String TASK_MESSAGE_LIST_PREFIX = "TASK:MESSAGES:";

    private StringRedisTemplate redisTemplate;
    private KafkaTemplate<String, String> kafkaTemplate;

    private BlockingQueue<DelayTaskMessage> delayTaskMessages = new DelayQueue<>();

    private boolean runnable = true;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void dispatch(String topic, String message, long timeout) {
        delayTaskMessages.add(new DelayTaskMessage(topic, message, timeout));
    }

    @Override
    public void destroy() throws Exception {
        // nothing ...
        runnable = false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this::execute);
        thread.setName("MsgSender");
        thread.setDaemon(true);
        thread.start();
    }

    private void execute() {
        while (runnable) {
            try {
                DelayTaskMessage take = delayTaskMessages.take();
                push(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加消息
     *
     * @param id      id
     * @param message msg
     */
    public void add(Serializable id, TaskMessage message) {
        redisTemplate.opsForList().rightPush(TASK_MESSAGE_LIST_PREFIX + id, message.stringify());
        redisTemplate.expire(TASK_MESSAGE_LIST_PREFIX + id, 30, TimeUnit.SECONDS);
    }

    /**
     * 释放消息
     *
     * @param id id
     */
    public void flush(Serializable id) {
        final String key = TASK_MESSAGE_LIST_PREFIX + id;
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > 0) {
            while (size-- > 0) {
                String message = redisTemplate.opsForList().leftPop(key);
                push(message);
            }
        }
        redisTemplate.delete(key);
    }

    private void push(TaskMessage msg) {
        kafkaTemplate.send(msg.getTopic(), msg.stringify());
        log.info("Kafka push -> [ topic = '{}', data = '{}' ]", msg.getTopic(), msg.stringify());
    }

    private void push(String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        try {
            JSONObject object = JSON.parseObject(message);
            if (object.containsKey("topic")) {
                String topic = object.getString("topic");
//                kafkaTemplate.send(topic, message);
//                log.info("Kafka push -> [ topic = '{}', data = '{}' ]", topic, message);
                delayTaskMessages.add(new DelayTaskMessage(topic, message, 2));
                return;
            }
            log.error("错误的消息格式 -> {}", message);
        } catch (JSONException ex) {
            log.error("错误的消息格式 -> {}", message);
            log.error("无效的消息", ex);
        }
    }
}
