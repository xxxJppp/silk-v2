package com.spark.bitrade;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PaymentApplication extends AdvancedApplication {

    public static void main(String[] args){
        SpringApplication.run(PaymentApplication.class,args);
    }
}
