package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.TestShardingSphereMapper;
import com.spark.bitrade.entity.TestShardingSphere;
import com.spark.bitrade.service.TestShardingSphereService;
import org.springframework.stereotype.Service;

/**
 * (TestShardingSphere)表服务实现类
 *
 * @author young
 * @since 2019-06-09 17:18:08
 */
@Service("testShardingSphereService")
public class TestShardingSphereServiceImpl extends ServiceImpl<TestShardingSphereMapper, TestShardingSphere> implements TestShardingSphereService {

}