package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.SilkPayGlobalConfigMapper;
import com.spark.bitrade.entity.SilkPayGlobalConfig;
import com.spark.bitrade.service.SilkPayGlobalConfigService;
import org.springframework.stereotype.Service;

/**
 * 全局基础配置(SilkPayGlobalConfig)表服务实现类
 *
 * @author wsy
 * @since 2019-08-21 14:22:18
 */
@Service("silkPayGlobalConfigService")
public class SilkPayGlobalConfigServiceImpl extends ServiceImpl<SilkPayGlobalConfigMapper, SilkPayGlobalConfig> implements SilkPayGlobalConfigService {

}