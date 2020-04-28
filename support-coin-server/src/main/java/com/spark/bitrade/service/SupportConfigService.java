package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.ModuleType;
import com.spark.bitrade.entity.SupportConfig;
import com.spark.bitrade.entity.SupportConfigList;

import java.util.List;

/**
 * <p>
 * 扶持上币配置 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportConfigService extends IService<SupportConfig> {
    /**
     * 根据模块获取支付配置
     * @param moduleType
     * @return
     */
    List<SupportConfigList> findByModule(ModuleType moduleType);

    SupportConfig findConfigByModule(ModuleType moduleType);
}
