package com.spark.bitrade.mqtt;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wsy
 * @since 2019/8/1 17:10
 */
@Component
public class MqttSender {

    @Resource
    private IMqttSender iMqttSender;

    /**
     * 发送给指定用户
     *
     * @param clientId clientId
     * @param command  指令
     * @param data     数据
     * @param <T>      数据类型
     */
    public <T> void sendToUser(String clientId, String command, T data) {
        if (data != null) {
            String topic = String.format("user/%s/%s", clientId, command);
            iMqttSender.sendToMqtt(topic, 2, JSON.toJSONString(data));
        }
    }

    /**
     * 发送给所有用户
     *
     * @param command 指令
     * @param data    数据
     * @param <T>     数据类型
     */
    public <T> void sendToAll(String command, T data) {
        if (data != null) {
            String topic = String.format("group/%s", command);
            iMqttSender.sendToMqtt(topic, 2, JSON.toJSONString(data));
        }
    }
}
