package com.spark.bitrade.mqtt.mapping;

import com.spark.bitrade.entity.MqttAppVersion;
import com.spark.bitrade.mqtt.annotations.MqttMapping;
import com.spark.bitrade.mqtt.annotations.MqttPayload;
import com.spark.bitrade.mqtt.annotations.MqttVariable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wsy
 * @since 2019/7/18 17:53
 */
@Slf4j
@Component
@MqttMapping("data/heartbeat")
public class HeartbeatHandler {

    @MqttMapping("/{clientId}")
    public void heartbeat(@MqttVariable("clientId") String clientId, @MqttPayload MqttAppVersion heartbeat) {
        log.info("{} --> {}", clientId, heartbeat);
    }

}
