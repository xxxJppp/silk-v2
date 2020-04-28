package com.spark.bitrade.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * PlaceScriptResultExecutor
 *
 * @author yangch
 * @since 2019/9/5 16:09
 */
public class PlaceScriptResultExecutor extends ScriptResultExecutor<Long> {

    public PlaceScriptResultExecutor(StringRedisTemplate redisTemplate, String path) {
        super(redisTemplate, path);
    }

    @Override
    protected ScriptResult<Long> convert(List list) {
        return new PlaceScriptResult(list);
    }
}
