package com.spark.bitrade.mqtt.mapping;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.MqttConnected;
import com.spark.bitrade.entity.MqttDeviceInfo;
import com.spark.bitrade.entity.MqttDisconnected;
import com.spark.bitrade.entity.SilkPayDevice;
import com.spark.bitrade.mqtt.annotations.MqttMapping;
import com.spark.bitrade.mqtt.annotations.MqttPayload;
import com.spark.bitrade.mqtt.annotations.MqttVariable;
import com.spark.bitrade.service.SilkPayDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wsy
 * @since 2019/7/29 18:06
 */
@Slf4j
@Component
public class ClientsHandler {

    @Resource
    private SilkPayDeviceService silkPayDeviceService;

    /**
     * 客户端上线
     *
     * @param node     节点
     * @param clientId clientId
     * @param data     数据
     */
    @MqttMapping("$SYS/brokers/{node}/clients/{clientId}/connected")
    public void connected(@MqttVariable("node") String node,
                          @MqttVariable("clientId") String clientId,
                          @MqttPayload MqttConnected data) {
        log.info("[ {} ] {} -> connected: {}", node, clientId, data);
        silkPayDeviceService.updateOnlineState(data.getUsername(), data.getClientid(), BooleanEnum.IS_TRUE);
    }

    /**
     * 客户端离线
     *
     * @param node     节点
     * @param clientId clientId
     * @param data     数据
     */
    @MqttMapping("$SYS/brokers/{node}/clients/{clientId}/disconnected")
    public void disconnected(@MqttVariable("node") String node,
                             @MqttVariable("clientId") String clientId,
                             @MqttPayload MqttDisconnected data) {
        log.info("[ {} ] {} -> disconnected: {}", node, clientId, data);
        silkPayDeviceService.updateOnlineState(data.getUsername(), data.getClientid(), BooleanEnum.IS_FALSE);
    }

    /**
     * 设备信息更新
     *
     * @param clientId clientId
     * @param info     信息
     */
    @MqttMapping("data/info/{clientId}/device")
    public void info(@MqttVariable("clientId") String clientId, @MqttPayload MqttDeviceInfo info) {
        log.info("{} --> {}", clientId, info);
        UpdateWrapper<SilkPayDevice> wrapper = new UpdateWrapper<>();
        wrapper.set("mac", info.getMac()).set("mobile", info.getMobile()).set("ip", info.getIp());
        wrapper.set("imei", info.getImei()).set("android_id", info.getAndroidId());
        wrapper.eq("serial_no", clientId);
        silkPayDeviceService.update(wrapper);
    }

    /**
     * 遗言
     *
     * @param clientId clientId
     * @param data     数据
     */
    @MqttMapping("will/{clientId}/disconnect")
    public void will(@MqttVariable("clientId") String clientId, @MqttPayload String data) {
        log.info("will {} -> {}", clientId, data);
    }
}
