package com.spark.bitrade;

import com.spark.bitrade.entity.constants.ExchangeRedisKeys;
import com.spark.bitrade.redis.PalceService;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.service.optfor.RedisKeyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *  
 *
 * @author young
 * @time 2019.09.17 14:25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class redisHashServiceTest {
    @Autowired
    private RedisHashService redisHashService;
    @Autowired
    private PalceService palceService;
    @Autowired
    private RedisKeyService redisKeyService;


    @Test
    public void delete() {
        String key = "data:exchangeOrder:BTCUSDT2:80043";
        String orderId1 = "S227871706797047808_BTCUSDT";
        String orderId2 = "S227863143680835584_BTCUSDT";
        //返回删除的数量
        long result1 = redisHashService.hDelete(key, orderId1, orderId2, "order3");
        System.out.println("result1:" + result1);

        //返回删除的数量
        long result2 = redisHashService.hDelete(key, "orderId");
        System.out.println("result2:" + result2);
    }

    /**
     * Redis lua脚本测试
     */
    @Test
    public void lock() {
        String orderId = "testorderId";

        boolean flag = isPlace(orderId);
        System.out.println("flag1=" + flag);

        boolean flag2 = isPlace(orderId);
        System.out.println("flag2=" + flag2);

    }

    @Test
    public void test2() {
        String key = ExchangeRedisKeys.getOrderTradingKey("testorderId");
        System.out.println(key);
        System.out.println(redisKeyService.hasKey(key));
    }

    private boolean isPlace(String orderId) {
        return palceService.place(new StringBuilder("lock:test:").append(orderId).toString(), 60);
    }
}
