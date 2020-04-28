package com.spark.bitrade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;
import java.util.Optional;

/**
 *  
 *
 * @author young
 * @time 2019.12.25 11:56
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "exchange.forward")
public class YamlForwardConfiguration {
    @NestedConfigurationProperty
    Map<String, ExchangeForwardStrategyConfiguration> strategys;



    /**
     * 获取策略
     *
     * @param symbol 交易对
     * @return
     */
    public Optional<ExchangeForwardStrategyConfiguration> getStrategy(String symbol) {
        return Optional.ofNullable(strategys.get(symbol));
    }
}
