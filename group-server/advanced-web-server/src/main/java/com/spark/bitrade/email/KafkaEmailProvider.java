package com.spark.bitrade.email;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.sms.SmsEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

/**
 * KafkaSMSProvider
 *
 * @author wsy
 * @since 2019/6/18 14:30
 */

@Slf4j
@Component
public class KafkaEmailProvider {

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

    /**
     * 发送邮件内容
     *
     * @param toEmail     接受邮箱地址
     * @param subject     邮件主题
     * @param htmlContent 邮件内容
     */
    public void sentEmailHtml(String toEmail, String subject, String htmlContent) {
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setToEmail(toEmail);
        emailEntity.setSubject(subject);
        emailEntity.setHtmlConent(htmlContent);
        kafkaTemplate.send(EmailEntity.MSG_EMAIL_HANDLER, toEmail, JSON.toJSONString(emailEntity));
    }
}
