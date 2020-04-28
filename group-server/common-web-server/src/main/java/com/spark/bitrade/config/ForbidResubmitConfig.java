package com.spark.bitrade.config;

import com.spark.bitrade.service.optfor.RedisStringService;
import com.spark.bitrade.web.resubmit.IForbidResubmit;
import com.spark.bitrade.web.resubmit.impl.ForbidResubmitDefaultImpl;
import com.spark.bitrade.web.resubmit.impl.ForbidResubmitRedisImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  禁止重复提交配置
 *
 * @author young
 * @time 2019.07.18 19:52
 */
@Configuration
public class ForbidResubmitConfig {

    @Configuration
    @ConditionalOnClass(name = "com.spark.bitrade.service.optfor.RedisStringService")
    class BuildOnRedis {
        @Bean
        public IForbidResubmit build(RedisStringService template) {
            return new ForbidResubmitRedisImpl(template);
        }
    }

    @Configuration
    @ConditionalOnMissingClass("com.spark.bitrade.service.optfor.RedisStringService")
    class BuildOnMissingRedis {
        @Bean
        public IForbidResubmit build() {
            return new ForbidResubmitDefaultImpl();
        }
    }
}
