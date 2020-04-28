package com.spark.bitrade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/***
 * 
 * @author yangch
 * @time 2018.09.23 13:38
 */
@Slf4j
@Component
public class ApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("======================应用数据初始化开始=====================");

        log.info("======================应用数据初始化完成=====================\r\n\r\n\r\n");
    }
}
