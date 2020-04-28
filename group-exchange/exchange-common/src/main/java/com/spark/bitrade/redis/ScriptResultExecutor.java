package com.spark.bitrade.redis;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;
import java.util.List;

/**
 * ScriptResultExecutor
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/5 15:57
 */
public abstract class ScriptResultExecutor<T> {

    private StringRedisTemplate redisTemplate;
    private DefaultRedisScript<List> script;

    public ScriptResultExecutor(StringRedisTemplate redisTemplate, String path) {
        this.redisTemplate = redisTemplate;

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setResultType(List.class);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        this.script = script;
    }

    /**
     * 执行脚本
     *
     * @param keys keys
     * @param args 参数， 必须能够转换为字符串...
     * @return result
     */
    public final ScriptResult<T> execute(List<String> keys, Object... args) {
        List execute = redisTemplate.execute(script, keys, args);
        return convert(execute);
    }

    /**
     * 执行脚本
     *
     * @param key key
     * @param arg 参数， 必须能够转换为字符串...
     * @return result
     */
    public final ScriptResult<T> execute(String key, Object arg) {
        List<String> keys = Collections.singletonList(key);
        return execute(keys, String.valueOf(arg));
    }

    protected abstract ScriptResult<T> convert(List list);
}
