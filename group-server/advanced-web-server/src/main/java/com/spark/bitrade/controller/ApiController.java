package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ApiController extends CommonController {

    public <T> MessageRespResult<IPage<T>> success(IPage<T> data) {
        return MessageRespResult.success4Data(data);
    }

    protected <V> MapBuilder<V> newMap(String key, V value) {
        return new MapBuilder<V>().put(key, value);
    }

    protected MapBuilder<Object> asMap(String key, Object value) {
        return new MapBuilder<>().put(key, value);
    }

    public class MapBuilder<V> {
        private Map<String, V> map = new HashMap<>();

        public MapBuilder<V> put(String key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<String, V> build() {
            return map;
        }
    }
}
