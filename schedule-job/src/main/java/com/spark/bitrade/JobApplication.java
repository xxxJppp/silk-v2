package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * JobApplication
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/8 18:10
 */
@EnableScheduling
@SpringBootApplication
public class JobApplication extends BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}
