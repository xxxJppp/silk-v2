package com.spark.bitrade.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * IncrDecrScriptResultExecutor
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/5 16:09
 */
public class IncrDecrScriptResultExecutor extends ScriptResultExecutor<Long> {

    public IncrDecrScriptResultExecutor(StringRedisTemplate redisTemplate, String path) {
        super(redisTemplate, path);
    }

    @Override
    protected ScriptResult<Long> convert(List list) {
        // System.out.println("list " + Arrays.toString(list.toArray()));
        return new IncrOrDecrScriptResult(list);
    }
}
