package com.spark.bitrade;


import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableDistributedTransaction
public class DemoApplication extends AdvancedApplication {

    public static void main(String[] args){
        SpringApplication.run(DemoApplication.class,args);
    }
}
