package com.spark.bitrade;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.spark.bitrade.LuckyTreasureServerApplication;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(LuckyTreasureServerApplication.class)
class LuckyTreasureServerApplicationTests {

	@Resource
	private StringRedisTemplate redisTemplate;
	
    @Test
    void contextLoads() {
    }

    @Test
    public void incTest() {
    	redisTemplate.opsForValue().increment("TEST_INC", 1);
    	System.out.println(redisTemplate.opsForValue().get("TEST_INC"));
    }
}
