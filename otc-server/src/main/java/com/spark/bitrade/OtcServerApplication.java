package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OtcServerApplication extends AdvancedApplication{

    public static void main(String[] args) {
       ApplicationContext ac =  SpringApplication.run(OtcServerApplication.class, args);
    }

}
