package com.spark.bitrade;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author young
 * @date 2019-05-10 10:32:29
 */
@EnableRetry
//开启事务，并设置order值，默认是Integer的最大值
@EnableTransactionManagement(order = 10)
//开启Druid监控
@ServletComponentScan("com.spark.bitrade.config")
public abstract class AdvancedApplication extends BaseApplication {
}
