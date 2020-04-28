package com.spark.bitrade.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *  
 *
 * @author young
 * @time 2019.09.18 10:03
 */
@Slf4j
@Service
public class PalceService implements InitializingBean {
    private StringRedisTemplate redisTemplate;
    private ScriptResultExecutor<Long> palceExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 加载脚本
        palceExecutor = new PlaceScriptResultExecutor(redisTemplate, "redis/place.lua");
    }

    @Autowired
    public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * 锁定指定的key
     *
     * @param key
     * @param lockTime 锁定时间，单位为秒
     * @return true=锁定成功，false=锁仓失败
     */
    public boolean place(String key, long lockTime) {
        return palceExecutor.execute(key, lockTime).isSuccess();
    }
}
