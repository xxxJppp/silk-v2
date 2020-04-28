package com.spark.bitrade.service;

import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.repository.entity.BtBankDataDict;
import com.spark.bitrade.repository.service.BtBankDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BtBankConfigService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BtBankDataDictService dictService;

    @SuppressWarnings("unchecked")
    public Object getConfig(String key) {
        String redisKey = BtBankSystemConfig.REDIS_DICT_PREFIX + key;
        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null) {
            BtBankDataDict one = dictService.findFirstByDictIdAndDictKey(BtBankSystemConfig.BT_BANK_MINER_CONFIG,
                    key);
            if (one != null) {
                o = one.getDictVal();
                redisTemplate.opsForValue().set(redisKey, o);
            }
        }

        if (o == null) {
            log.warn(" BtBankSystemConfig [{}] value not exists", key);
        }
        return o;
    }
}
