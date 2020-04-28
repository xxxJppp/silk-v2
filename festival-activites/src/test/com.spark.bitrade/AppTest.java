package com.spark.bitrade;

import com.spark.bitrade.biz.MiningActivityBizService;
import com.spark.bitrade.common.ReidsKeyGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Zhong Jiang
 * @date: 2019-12-31 9:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Autowired
    private MiningActivityBizService activityBizService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void doMiningTest() {
        redisTemplate.opsForList().remove(ReidsKeyGenerator.getMineralNumberNotZeroKey(), 0, 2);
    }
}
