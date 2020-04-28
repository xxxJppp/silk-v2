package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SilkPayGlobalConfig;
import com.spark.bitrade.entity.SilkPayUserConfig;
import com.spark.bitrade.mapper.SilkPayUserConfigMapper;
import com.spark.bitrade.service.SilkPayUserConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户支付配置(SilkPayUserConfig)表服务实现类
 *
 * @author wsy
 * @since 2019-08-21 14:22:00
 */
@Service("silkPayUserConfigService")
public class SilkPayUserConfigServiceImpl extends ServiceImpl<SilkPayUserConfigMapper, SilkPayUserConfig> implements SilkPayUserConfigService {

    @Resource
    private SilkPayUserConfigMapper silkPayUserConfigMapper;

    @Override
    public Boolean resetSurplus(Long memberId, SilkPayGlobalConfig globalConfig) {
        return silkPayUserConfigMapper.resetSurplus(memberId, globalConfig) > 0;
    }

    @Override
    public SilkPayUserConfig getUserConfig(Long memberId) {
        return silkPayUserConfigMapper.getUserConfig(memberId);
    }
}
