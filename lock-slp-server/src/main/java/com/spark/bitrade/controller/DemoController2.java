package com.spark.bitrade.controller;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequestMapping("/demo2")
@RestController
public class DemoController2 {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/param/post")
    public Object test(@RequestBody Map<String, String> param) {
        log.info("post params => {}", param);
        return Collections.singletonMap("a", "b");
    }

    @GetMapping("/param/get")
    public Object xx(int a, int b) {
        log.info("post a => {}, b => {}", a, b);
        return null;
    }


    //@Cacheable(cacheNames = "memberOtc", key = "'entity:memberOtc'")
    @GetMapping("/cache")
    public Object cache(@MemberAccount Member member2) {
        log.info("--请求用户信息--:{}", member2);

        Member member = new Member();
        member.setId(0L);
//        member.setApiKey("2222");
//        member.setApiSecret("3333");
//        member.setOtcAllow(BooleanEnum.IS_TRUE);
//        member.setStatus(BooleanEnum.IS_FALSE);
//        member.setCreateTime(new Date());
//        member.setUpdateTime(new Date());
        log.info("--生成的数据：{}", member);

        return member;
    }




    /**
     * kafka发送消息示例
     *
     * @return
     */
    @GetMapping("/sendKafka")
    public MessageRespResult sendKafka() {
//        CollectCarrier carrier = new CollectCarrier();
//        carrier.setCollectType(CollectActionEventType.COIN_OUT);
//        carrier.setMemberId("1");
//        carrier.setRefId("1111");
//        carrier.setExtend("Extend");
//        carrier.setCreateTime(new Date());
//        carrier.setLocale("zh");
//
        //kafkaTemplate.send("MSG_TEST", "MSG_TEST", JSON.toJSONString(carrier));
        kafkaTemplate.send("MSG_TEST", "MSG_TEST", "kafka发送消息示例222");

        return MessageRespResult.success();
    }


    /**
     * kafka接收消息示例
     *
     * @param record
     */
    @KafkaListener(topics = "MSG_TEST")
    public void handleMessage(ConsumerRecord<String, String> record) {
        System.out.println("接收kafka消息：" + record);
    }
}
