package com.spark.bitrade;


//import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDistributedTransaction
public class AccountApplication extends AdvancedApplication {

    public static void main(String[] args){
        SpringApplication.run(AccountApplication.class,args);
    }
}
