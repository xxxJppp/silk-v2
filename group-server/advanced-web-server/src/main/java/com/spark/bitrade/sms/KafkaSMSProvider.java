package com.spark.bitrade.sms;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * KafkaSMSProvider
 *
 * @author wsy
 * @since 2019/6/18 14:30
 */

@Slf4j
@Component
public class KafkaSMSProvider {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private SmsEntity createSmsEntity(String platform, String area, String mobile, String content) {
        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setThirdMark(platform);
        smsEntity.setAreaCode(area);
        smsEntity.setToPhone(mobile);
        smsEntity.setValidDate(new Date());
        smsEntity.setContent(content);
        return smsEntity;
    }

    private void sendKafkaMessage(String key, String data) {
        kafkaTemplate.send(SmsEntity.MSG_SMS_HANDLER, key, data);
    }

    @Async
    public void sendVoiceSms(String platform, String area, String mobile, String code) {
        sendKafkaMessage("sendVoiceCode", JSON.toJSONString(createSmsEntity(platform, area, mobile, code)));
    }

    @Async
    public void sendSms(String platform, String area, String mobile, String content) {
        sendKafkaMessage("sendSingleMessage", JSON.toJSONString(createSmsEntity(platform, area, mobile, content)));
    }

    @Async
    public void batchSendSms(String platform, String area, String mobile, String text) {
        sendKafkaMessage("batchSend", JSON.toJSONString(createSmsEntity(platform, area, mobile, text)));
    }
}
