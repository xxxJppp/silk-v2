package com.spark.bitrade.config;

import com.spark.bitrade.support.ArchiveScheduleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ArchiveConfiguration
 *
 * @author Pikachu
 * @since 2019/11/4 17:23
 */
@Configuration
public class ArchiveConfiguration {

    @Bean
    public ArchiveScheduleService buildArchiveScheduleService() {
        return new ArchiveScheduleService();
    }

}
