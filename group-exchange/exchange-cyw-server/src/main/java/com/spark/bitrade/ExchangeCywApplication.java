package com.spark.bitrade;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class ExchangeCywApplication extends AdvancedApplication {

    public static void main(String[] args){
        SpringApplication.run(ExchangeCywApplication.class,args);
    }
}
