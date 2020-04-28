package com.spark.bitrade.config;

import com.spark.bitrade.util.IdWorkByTwitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhang Jinwei
 * @since 2017年12月22日
 */
@Configuration
public class SystemConfig {

    @Bean
    public IdWorkByTwitter idWorkByTwitter(@Value("${spark.system.work-id:0}") long workId, @Value("${spark.system.data-center-id:0}") long dataCenterId) {
        return new IdWorkByTwitter(workId, dataCenterId);
    }

}
