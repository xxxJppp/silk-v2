package com.spark.bitrade.job.config;

import com.spark.bitrade.job.XxlJobOptions;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * XxlJobConfiguration
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/8 18:08
 */
@Configuration
@ComponentScan(basePackages = "com.spark.bitrade.job.handler")
public class XxlJobConfiguration {

    @Bean
    @ConfigurationProperties(prefix = XxlJobOptions.PREFIX)
    public XxlJobOptions xxlJobOptions() {
        return new XxlJobOptions();
    }

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor(XxlJobOptions options) {
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        options.setup(xxlJobExecutor);
        return xxlJobExecutor;
    }
}
