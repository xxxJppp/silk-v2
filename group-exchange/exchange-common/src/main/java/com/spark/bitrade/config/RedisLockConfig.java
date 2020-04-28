package com.spark.bitrade.config;

import com.spark.bitrade.lock.DistributedLockTemplate;
import com.spark.bitrade.lock.redis.RedisDistributedLockTemplate;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisLockConfig
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/19 14:15
 */
@Configuration
public class RedisLockConfig {

    private final RedisProperties properties;

    public RedisLockConfig(RedisProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DistributedLockTemplate buildDistributedLockTemplate() {
        return new RedisDistributedLockTemplate(buildJedisPool());
    }

    private JedisPool buildJedisPool() {
        return new JedisPool(jedisPoolConfig(), this.properties.getHost(), this.properties.getPort(),
                this.properties.getTimeout(), this.properties.getPassword());
    }

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool props = this.properties.getPool();

        if (props == null) {
            props = new RedisProperties.Pool();
        }

        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis(props.getMaxWait());
        return config;
    }
}
