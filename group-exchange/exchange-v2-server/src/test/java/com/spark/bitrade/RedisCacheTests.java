package com.spark.bitrade;

import com.spark.bitrade.redis.IncrDecrScriptResultExecutor;
import com.spark.bitrade.redis.ScriptResult;
import com.spark.bitrade.redis.ScriptResultExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 *  
 *
 * @author young
 * @time 2019.09.05 18:07
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
public class RedisCacheTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testIncrease() {
        String key = "exchange:wallet:SLP:7001";
        System.out.println(redisTemplate.hasKey(key));

        Double increment = redisTemplate.opsForValue().increment(key, 0.5453445345d);
        System.out.println(increment);
    }

    @Test
    public void testIncrease2() {
        String key = "data:wallet:SLP:71639";
        redisTemplate.opsForValue().get(key);

        long startTime = System.currentTimeMillis();


        for (int i = 0; i < 10000; i++) {
            //redisTemplate.opsForValue().get(key);

            redisTemplate.opsForValue().increment(key, 1);
        }
        System.out.println("time1:" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
//        for (int i = 0; i <10000 ; i++) {
//            redisTemplate.opsForValue().get(key);
//        }

        System.out.println("time2:" + (System.currentTimeMillis() - startTime));

        //System.out.println(redisTemplate.hasKey(key));

        //Double increment = redisTemplate.opsForValue().increment(key, 0.5453445345d);
        //System.out.println(increment);
    }

    @Test
    public void testScript() {
        String key = "data:wallet:SLP:71639";
//        redisTemplate.opsForValue().increment("data:wallet:SLP:71639", 100000);
        redisTemplate.opsForValue().increment(key, 100000L);
//        DefaultRedisScript<List> script = new DefaultRedisScript<>();
//        script.setResultType(List.class);
//        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/demo.lua")));
//
//        List<String> keys = new ArrayList<>();
//        keys.add("lua:test");

        ScriptResultExecutor<Long> executor = new IncrDecrScriptResultExecutor(redisTemplate, "redis/decrby.lua");

//        List execute = redisTemplate.execute(script, keys, "99");
//        System.out.println(Arrays.toString(execute.toArray(new Object[]{})));


        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 1; i++) {
            Thread thread = new Thread(() -> {
                ScriptResult<Long> execute = executor.execute(key, 99L);
                System.out.println("success = " + execute.isSuccess() + " ,value = " + execute.getResult());
                latch.countDown();
            });
            thread.setName("ddddd ---- " + i);
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
