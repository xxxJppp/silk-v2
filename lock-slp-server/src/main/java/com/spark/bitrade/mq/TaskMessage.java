package com.spark.bitrade.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * 任务消息接口
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/3 11:20
 */
public interface TaskMessage {

    /**
     * 业务ID
     *
     * @return id
     */
    String getRefId();

    /**
     * 字符串化
     *
     * @return string
     */
    String stringify();

    /**
     * 消息主题
     *
     * @return topic
     */
    default String getTopic() {
        return "";
    }

    /**
     * 转换为任务消息
     * <p>
     * 指定KAFKA主题
     *
     * @param topic 主题
     * @return 任务消息
     */
    default TaskMessage toTaskMessage(final String topic) {

        final TaskMessage tm = this;

        return new TaskMessage() {
            @Override
            public String getRefId() {
                return tm.getRefId();
            }

            @Override
            public String stringify() {
                JSONObject object = JSON.parseObject(tm.stringify());
                object.put("topic", topic);
                return object.toJSONString();
            }

            @Override
            public String getTopic() {
                return topic;
            }
        };
    }

    /**
     * 包装函数
     *
     * @param messages 消息
     * @return 返回消息数组
     */
    static List<TaskMessage> wrap(TaskMessage... messages) {
        return Arrays.asList(messages);
    }
}
