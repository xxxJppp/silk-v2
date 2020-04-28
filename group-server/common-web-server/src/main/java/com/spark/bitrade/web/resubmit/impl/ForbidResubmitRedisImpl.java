package com.spark.bitrade.web.resubmit.impl;

import com.spark.bitrade.service.optfor.RedisStringService;
import com.spark.bitrade.web.resubmit.IForbidResubmit;

import java.util.concurrent.TimeUnit;

/**
 *  基于redis的 禁用重复提交
 * 备注：默认5秒内禁止重复提交
 *
 * @author young
 * @time 2019.07.18 20:03
 */
public class ForbidResubmitRedisImpl implements IForbidResubmit {

    private final RedisStringService redisTemplate;

    public ForbidResubmitRedisImpl(RedisStringService redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean validate(String key, long interdictTime) {
        if (redisTemplate.get(key) == null) {
            redisTemplate.setEx(key, "1", interdictTime, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }
}
