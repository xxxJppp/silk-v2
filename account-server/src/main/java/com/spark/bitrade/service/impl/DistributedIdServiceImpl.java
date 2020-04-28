package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.service.IDistributedIdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *  
 *
 * @author yangch
 * @time 2019.01.22 11:29
 */
@Slf4j
@Service
public class DistributedIdServiceImpl implements IDistributedIdService {
    @Override
    public long generateId() {
        // todo 待完善为支持分布式ID
        return IdWorker.getId();
    }

    @Override
    public String generateStrId() {
        return String.valueOf(generateId());
    }

    @Override
    public String generateStrId(String prefix) {
        return new StringBuilder(prefix).append(generateId()).toString();
    }
}
