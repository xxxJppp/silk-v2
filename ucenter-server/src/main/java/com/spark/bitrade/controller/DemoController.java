package com.spark.bitrade.controller;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequestMapping
@RestController
public class DemoController {

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


    @Cacheable(cacheNames = "memberUc", key = "'entity:memberUc'")
    @GetMapping("/cache")
    public Object cache(@MemberAccount Member member2) {
        log.info("--请求用户信息--:{}", member2);

        Member member = new Member();
        member.setId(3L);
//        member.setApiKey("222244");
//        member.setApiSecret("3333");
//        member.setOtcAllow(BooleanEnum.IS_TRUE);
//        member.setStatus(BooleanEnum.IS_FALSE);
//        member.setCreateTime(new Date());
//        member.setUpdateTime(new Date());
        log.info("--生成的数据：{}", member);

        return member;
    }
}
