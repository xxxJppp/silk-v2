package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author davi
 */
@SpringBootApplication
@EnableAsync
public class BtBankApplication extends AdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(BtBankApplication.class, args);
    }
}
