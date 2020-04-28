package com.spark.bitrade;


import com.spark.bitrade.config.YamlForwardConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableConfigurationProperties({YamlForwardConfiguration.class})
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExchangeV2Application extends AdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeV2Application.class, args);
    }
}
