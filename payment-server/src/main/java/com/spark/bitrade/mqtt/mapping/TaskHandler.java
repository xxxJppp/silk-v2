package com.spark.bitrade.mqtt.mapping;

import com.spark.bitrade.entity.MqttPayState;
import com.spark.bitrade.mapper.SilkPayAccountMapper;
import com.spark.bitrade.mqtt.annotations.MqttMapping;
import com.spark.bitrade.mqtt.annotations.MqttPayload;
import com.spark.bitrade.mqtt.annotations.MqttVariable;
import com.spark.bitrade.service.SilkPayAccountService;
import com.spark.bitrade.service.SilkPayDeviceService;
import com.spark.bitrade.service.SilkPayMatchRecordService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wsy
 * @since 2019/7/18 17:39
 */
@Slf4j
@Component
@MqttMapping("data/task")
public class TaskHandler {

    @Autowired
    private SilkPayMatchRecordService silkPayMatchRecordService;

    @Autowired
    private SilkPayAccountMapper silkPayAccountMapper;

    @MqttMapping("/{clientId}/state")
    public void payTaskState(@MqttVariable("clientId") String clientId, @MqttPayload MqttPayState data) {
        log.info("接收到消息 clientId {} data {}", clientId, data);
        // 验证设备是否合法
        String serialNo = silkPayAccountMapper.findMatchRecordDeviceSerialNo(data.getId());

                // ACCEPT(2000, "下发成功"),
                // SUCCESS(2001, "付款成功"),
                // COMPLETE(2003, "付款完成"),
                //
                // EXCEPTION(5000, "系统异常，支付失败"),
                // OPEN_PAY_ERROR(5002, "调起支付失败"),
                // BALANCE_LACK(5003, "余额不足"),
                //
                // RUNNING(6001, "付款繁忙，请稍候"),
                // INCONSISTENT_AMOUNT(6000, "付款金额不符");
        log.info("设备编号 clientId {} serialNo {}", clientId, serialNo);
        if (data != null && StringUtils.equalsIgnoreCase(serialNo, clientId)) {
            switch (data.getCode()) {
                case 2000:
                    // 下发成功
                    silkPayMatchRecordService.distributeSuccess(data);
                    break;
                case 2001:
                    // 付款成功
                    silkPayMatchRecordService.paySuccessed(clientId, data);
                    break;
                case 2003:
                    // 付款完成
                    silkPayMatchRecordService.asyncDistribute(clientId);
                    break;
                case 6001:
                    log.info("设备编号 clientId {} serialNo {} 付款繁忙，请稍候 {}", clientId, serialNo,data);
                    break;
                default:
                    // 付款失败
                    silkPayMatchRecordService.payFailed(data);
            }
        }
    }

}
