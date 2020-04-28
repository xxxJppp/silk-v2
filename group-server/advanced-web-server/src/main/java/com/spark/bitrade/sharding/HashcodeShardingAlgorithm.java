package com.spark.bitrade.sharding;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

/**
 *  hash分片
 *
 * @author young
 * @time 2019.09.24 11:48
 */
public class HashcodeShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<String> shardingValue) {
        return (String) tableNames.toArray()[Math.abs(shardingValue.getValue().hashCode() % tableNames.size())];
//
//        for (String each : tableNames) {
//            if (shardingValue.getValue().hashCode() % tableNames.size() == 0) {
//                return each;
//            }
//        }
//        throw new UnsupportedOperationException();
    }
}
