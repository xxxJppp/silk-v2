package com.spark.bitrade.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RedisUtil {
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
    }

    public void setVal(String key , String val) {
    	this.redisTemplate.opsForValue().set(key, val);
    }
    
    public Object getVal(String key) {
    	return this.redisTemplate.opsForValue().get(key);
    }
    
    public long increment(String key, long time) {
    	long inc = this.redisTemplate.opsForValue().increment(key, 1);
        this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
        return inc;
    }
    
    public boolean keyExist(String... keys) {
        for (String key : keys) {
            if (!this.redisTemplate.hasKey(key)) return false;
        }
        return true;
    }
    
    public void leftPush(String key , String val) {
    	this.redisTemplate.opsForList().leftPush(key, val);
    }
    
    public String rightPop(String key) {
    	try {
			return this.redisTemplate.opsForList().rightPop(key).toString();
		} catch (Exception e) {
			return null;
		}
    } 
}
