package com.spark.bitrade.trans;

import com.spark.bitrade.redis.IncrDecrScriptResultExecutor;
import com.spark.bitrade.redis.ScriptResult;
import com.spark.bitrade.redis.ScriptResultExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * RedisLuaTests
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/5 9:57
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class RedisLuaTests {

    private StringRedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    @Test
    public void testScript() {
         // redisTemplate.opsForValue().increment("lua:test:BT", 100);
//        DefaultRedisScript<List> script = new DefaultRedisScript<>();
//        script.setResultType(List.class);
//        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/demo.lua")));
//
//        List<String> keys = new ArrayList<>();
//        keys.add("lua:test");

        ScriptResultExecutor<Long> executor = new IncrDecrScriptResultExecutor(redisTemplate, "redis/decrby.lua");

//        List execute = redisTemplate.execute(script, keys, "99");
//        System.out.println(Arrays.toString(execute.toArray(new Object[]{})));


        CountDownLatch latch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            Thread thread = new Thread(() -> {
                ScriptResult<Long> execute = executor.execute("lua:test:BT", 10);
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
