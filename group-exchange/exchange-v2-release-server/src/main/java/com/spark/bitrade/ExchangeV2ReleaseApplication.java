package com.spark.bitrade;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExchangeV2ReleaseApplication extends AdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeV2ReleaseApplication.class, args);
    }
}
