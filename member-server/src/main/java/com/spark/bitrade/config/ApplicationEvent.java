package com.spark.bitrade.config;

//import com.spark.bitrade.service.ExchangeMemberDiscountRuleService;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class ApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {
//    @Autowired
//    private ExchangeMemberDiscountRuleService exchangeMemberDiscountRuleService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("---------------init----------------------");
    }
}
