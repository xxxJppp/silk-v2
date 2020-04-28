package com.spark.bitrade;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author young
 * @date 2019-05-10 10:32:29
 */
@EnableEurekaClient
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableFeignClients
//开启事务，并设置order值，默认是Integer的最大值
//@EnableTransactionManagement(order = 10)
//开启Druid监控
//@ServletComponentScan("com.spark.bitrade.config")
public abstract class BaseApplication {
}
