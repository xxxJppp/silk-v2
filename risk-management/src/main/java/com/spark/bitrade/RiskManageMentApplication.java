package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.spark.bitrade.service.CoinService;

@SpringBootApplication
public class RiskManageMentApplication extends AdvancedApplication {

	
	public static void main(String[] args) {
        SpringApplication.run(RiskManageMentApplication.class, args);
    }
}
