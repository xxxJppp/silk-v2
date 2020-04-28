package com.spark.bitrade.config;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * IdWorkerConfiguration
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/19 14:05
 */
@Slf4j
@Configuration
public class IdWorkerConfiguration {

    private final StringRedisTemplate redisTemplate;
    private final Environment environment;

    public IdWorkerConfiguration(StringRedisTemplate redisTemplate, Environment environment) throws Exception {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.init();
    }

    private void init() throws UnknownHostException {

        String appName = environment.getProperty("spring.application.name", "cyw");
        String port = environment.getProperty("server.port", "1000");

        InetAddress localHost = InetAddress.getLocalHost();
        String hostAddress = localHost.getHostAddress();

        // redis key prefix
        final String prefix = "sequence:" + appName;

        final String value = hostAddress + ":" + appName + ":" + port;

        final ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // 已经注册的
        Set<String> keys = redisTemplate.keys(prefix + ":*");
        List<KV> exists = keys.stream()
                .map(key -> {
                    String val = operations.get(key);
                    return new KV(key, val);
                }).collect(Collectors.toList());

        // 已经存在
        if (exists.size() > 0) {
            for (KV exist : exists) {
                if (exist.value.equals(value)) {
                    log.info("ID-Worker 已经存在 workerId = {}, dataCenterId = {}", exist.workerId, exist.dataCenterId);
                    IdWorker.initSequence(exist.workerId, exist.dataCenterId);
                    return;
                }
            }
        }

        // 验证
        for (long dataCenterId = 0; dataCenterId < 32; dataCenterId++) {
            for (long workId = 0; workId < 32; workId++) {

                String key = prefix + ":" + String.format("%d-%d", dataCenterId, workId);
                if (keys.contains(key)) {
                    // 已经使用
                    continue;
                }

                // 保存成功
                Boolean save = operations.setIfAbsent(key, value);
                if (save != null && save) {
                    log.info("ID-Worker 已经指定 workerId = {}, dataCenterId = {}", workId, dataCenterId);
                    IdWorker.initSequence(workId, dataCenterId);
                    return;
                }
            }
        }

        log.warn("ID-Worker 未指定，使用默认设置存在风险");

    }

    @Getter
    public static class KV {
        private final String key;
        private final String value;

        private final long dataCenterId;
        private final long workerId;

        KV(String key, String value) {
            this.key = key;
            this.value = value;

            String[] split = key.split(":");
            if (split.length != 3) {
                throw new IllegalArgumentException("无效的Key");
            }
            String[] data = split[2].split("-");

            if (data.length != 2) {
                throw new IllegalArgumentException("无效的Key");
            }

            this.dataCenterId = NumberUtils.toLong(data[0]);
            this.workerId = NumberUtils.toLong(data[1]);
        }
    }
}
