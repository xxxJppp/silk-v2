package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LuckyTreasureServerApplication extends AdvancedApplication{

    public static void main(String[] args) {
       ApplicationContext ac =  SpringApplication.run(LuckyTreasureServerApplication.class, args);
    }

}
