package com.spark.bitrade;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Calendar;

/**
 *  
 *
 * @author young
 * @time 2019.09.30 10:00
 */
public class Test2 {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        // 1569808876477

        Calendar calendar = Calendar.getInstance();
        System.out.println(Duration.ofDays(1).toMillis());
        Instant instant = Instant.now();
        LocalTime.now();

        System.out.println((System.currentTimeMillis() -1569808876477L) /1000 > 40);
    }
}
