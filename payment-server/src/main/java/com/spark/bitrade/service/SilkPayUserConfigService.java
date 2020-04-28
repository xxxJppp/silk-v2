package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SilkPayGlobalConfig;
import com.spark.bitrade.entity.SilkPayUserConfig;

/**
 * 用户支付配置(SilkPayUserConfig)表服务接口
 *
 * @author wsy
 * @since 2019-08-21 14:22:00
 */
public interface SilkPayUserConfigService extends IService<SilkPayUserConfig> {

    /**
     * 解限用户数据
     *
     * @return 解限状态
     */
    Boolean resetSurplus(Long memberId, SilkPayGlobalConfig globalConfig);

    /**
     * 查询用户支付配置
     *
     * @param memberId
     * @return
     */
    SilkPayUserConfig getUserConfig(Long memberId);
}
